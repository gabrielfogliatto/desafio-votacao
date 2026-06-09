package br.com.cooperativa.votacao.api.dto;

import br.com.cooperativa.votacao.domain.VoteOption;
import br.com.cooperativa.votacao.domain.VotingStatus;

import java.time.Instant;

public record VotingResultResponse(
        Long agendaId,
        VotingStatus status,
        Instant closesAt,
        long sim,
        long nao,
        long total,
        VoteOption winner
) {
}
