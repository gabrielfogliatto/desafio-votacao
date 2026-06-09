package br.com.cooperativa.votacao.api.dto;

import br.com.cooperativa.votacao.domain.VotingSession;
import br.com.cooperativa.votacao.domain.VotingStatus;

import java.time.Instant;

public record VotingSessionResponse(
        Long id,
        Long agendaId,
        Instant openedAt,
        Instant closesAt,
        VotingStatus status
) {

    public static VotingSessionResponse from(VotingSession session, Instant now) {
        return new VotingSessionResponse(
                session.getId(),
                session.getAgenda().getId(),
                session.getOpenedAt(),
                session.getClosesAt(),
                session.statusAt(now)
        );
    }
}
