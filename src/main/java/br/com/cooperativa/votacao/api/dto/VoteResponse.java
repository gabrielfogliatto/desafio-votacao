package br.com.cooperativa.votacao.api.dto;

import br.com.cooperativa.votacao.domain.Vote;
import br.com.cooperativa.votacao.domain.VoteOption;

import java.time.Instant;

public record VoteResponse(
        Long id,
        Long agendaId,
        String associateId,
        VoteOption vote,
        Instant createdAt
) {

    public static VoteResponse from(Vote vote) {
        return new VoteResponse(
                vote.getId(),
                vote.getAgenda().getId(),
                vote.getAssociateId(),
                vote.getOption(),
                vote.getCreatedAt()
        );
    }
}
