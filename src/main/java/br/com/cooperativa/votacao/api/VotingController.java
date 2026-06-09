package br.com.cooperativa.votacao.api;

import br.com.cooperativa.votacao.api.dto.OpenSessionRequest;
import br.com.cooperativa.votacao.api.dto.RegisterVoteRequest;
import br.com.cooperativa.votacao.api.dto.VoteResponse;
import br.com.cooperativa.votacao.api.dto.VotingResultResponse;
import br.com.cooperativa.votacao.api.dto.VotingSessionResponse;
import br.com.cooperativa.votacao.service.VotingService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/agendas/{agendaId}")
public class VotingController {

    private final VotingService votingService;

    public VotingController(VotingService votingService) {
        this.votingService = votingService;
    }

    @Operation(summary = "Abrir uma sessão de votação para a pauta")
    @PostMapping("/sessions")
    @ResponseStatus(HttpStatus.CREATED)
    public VotingSessionResponse openSession(
            @PathVariable Long agendaId,
            @Valid @RequestBody(required = false) OpenSessionRequest request
    ) {
        return votingService.openSession(agendaId, request == null ? new OpenSessionRequest(null) : request);
    }

    @Operation(summary = "Registrar voto do associado")
    @PostMapping("/votes")
    @ResponseStatus(HttpStatus.CREATED)
    public VoteResponse vote(
            @PathVariable Long agendaId,
            @Valid @RequestBody RegisterVoteRequest request
    ) {
        return votingService.registerVote(agendaId, request);
    }

    @Operation(summary = "Obter resultado da votação de uma pauta")
    @GetMapping("/result")
    public VotingResultResponse result(@PathVariable Long agendaId) {
        return votingService.result(agendaId);
    }
}
