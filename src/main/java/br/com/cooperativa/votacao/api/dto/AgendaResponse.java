package br.com.cooperativa.votacao.api.dto;

import br.com.cooperativa.votacao.domain.Agenda;

import java.time.Instant;

public record AgendaResponse(
        Long id,
        String title,
        String description,
        Instant createdAt
) {

    public static AgendaResponse from(Agenda agenda) {
        return new AgendaResponse(
                agenda.getId(),
                agenda.getTitle(),
                agenda.getDescription(),
                agenda.getCreatedAt()
        );
    }
}
