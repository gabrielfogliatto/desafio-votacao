package br.com.cooperativa.votacao.api.dto;

import jakarta.validation.constraints.Positive;

public record OpenSessionRequest(
        @Positive Integer durationSeconds
) {
}
