package br.com.cooperativa.votacao.service;

import br.com.cooperativa.votacao.api.dto.OpenSessionRequest;
import br.com.cooperativa.votacao.api.dto.RegisterVoteRequest;
import br.com.cooperativa.votacao.api.dto.VoteResponse;
import br.com.cooperativa.votacao.api.dto.VotingResultResponse;
import br.com.cooperativa.votacao.api.dto.VotingSessionResponse;
import br.com.cooperativa.votacao.domain.Agenda;
import br.com.cooperativa.votacao.domain.Vote;
import br.com.cooperativa.votacao.domain.VoteOption;
import br.com.cooperativa.votacao.domain.VotingSession;
import br.com.cooperativa.votacao.domain.VotingStatus;
import br.com.cooperativa.votacao.repository.VoteCountProjection;
import br.com.cooperativa.votacao.repository.VoteRepository;
import br.com.cooperativa.votacao.repository.VotingSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

@Service
public class VotingService {

    private static final Logger log = LoggerFactory.getLogger(VotingService.class);

    private final AgendaService agendaService;
    private final VotingSessionRepository sessionRepository;
    private final VoteRepository voteRepository;
    private final ExternalCpfClient cpfClient;
    private final Clock clock;
    private final Duration defaultDuration;

    public VotingService(
            AgendaService agendaService,
            VotingSessionRepository sessionRepository,
            VoteRepository voteRepository,
            ExternalCpfClient cpfClient,
            Clock clock,
            @Value("${app.voting-session.default-duration}") Duration defaultDuration
    ) {
        this.agendaService = agendaService;
        this.sessionRepository = sessionRepository;
        this.voteRepository = voteRepository;
        this.cpfClient = cpfClient;
        this.clock = clock;
        this.defaultDuration = defaultDuration;
    }

    @Transactional
    public VotingSessionResponse openSession(Long agendaId, OpenSessionRequest request) {
        Agenda agenda = agendaService.getOrThrow(agendaId);
        if (sessionRepository.existsByAgendaId(agendaId)) {
            throw new BusinessException(HttpStatus.CONFLICT, "Voting session already exists for this agenda");
        }

        Duration duration = request.durationSeconds() == null
                ? defaultDuration
                : Duration.ofSeconds(request.durationSeconds());
        Instant openedAt = Instant.now(clock);
        VotingSession session = sessionRepository.save(new VotingSession(agenda, openedAt, openedAt.plus(duration)));
        log.info("Opened voting session id={} agendaId={} closesAt={}", session.getId(), agendaId, session.getClosesAt());
        return VotingSessionResponse.from(session, openedAt);
    }

    @Transactional
    public VoteResponse registerVote(Long agendaId, RegisterVoteRequest request) {
        Agenda agenda = agendaService.getOrThrow(agendaId);
        VotingSession session = sessionRepository.findByAgendaId(agendaId)
                .orElseThrow(() -> new BusinessException(HttpStatus.CONFLICT, "Voting session is not open"));

        if (session.statusAt(Instant.now(clock)) == VotingStatus.CLOSED) {
            throw new BusinessException(HttpStatus.CONFLICT, "Voting session is closed");
        }

        CpfVotingStatus cpfStatus = cpfClient.check(request.cpf());
        if (cpfStatus == CpfVotingStatus.INVALID || cpfStatus == CpfVotingStatus.UNABLE_TO_VOTE) {
            throw new ResourceNotFoundException("Associate is not able to vote");
        }

        String associateId = request.associateId().trim();
        if (voteRepository.existsByAgendaIdAndAssociateId(agendaId, associateId)) {
            throw new BusinessException(HttpStatus.CONFLICT, "Associate already voted on this agenda");
        }

        try {
            Vote vote = voteRepository.save(new Vote(agenda, associateId, request.cpf(), request.vote()));
            log.info("Registered vote id={} agendaId={} associateId={}", vote.getId(), agendaId, associateId);
            return VoteResponse.from(vote);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(HttpStatus.CONFLICT, "Associate already voted on this agenda");
        }
    }

    @Transactional(readOnly = true)
    public VotingResultResponse result(Long agendaId) {
        VotingSession session = sessionRepository.findByAgendaId(agendaId)
                .orElseThrow(() -> new ResourceNotFoundException("Voting session not found"));

        Map<VoteOption, Long> counts = new EnumMap<>(VoteOption.class);
        counts.put(VoteOption.SIM, 0L);
        counts.put(VoteOption.NAO, 0L);
        for (VoteCountProjection projection : voteRepository.countVotesByAgenda(agendaId)) {
            counts.put(projection.getOption(), projection.getTotal());
        }

        long sim = counts.get(VoteOption.SIM);
        long nao = counts.get(VoteOption.NAO);
        VoteOption winner = sim == nao ? null : (sim > nao ? VoteOption.SIM : VoteOption.NAO);
        return new VotingResultResponse(
                agendaId,
                session.statusAt(Instant.now(clock)),
                session.getClosesAt(),
                sim,
                nao,
                sim + nao,
                winner
        );
    }
}
