package br.com.cooperativa.votacao.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAgendaRequest(
        @NotBlank @Size(max = 160) String title,
        @NotBlank @Size(max = 2000) String description
) {
}
