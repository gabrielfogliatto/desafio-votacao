package br.com.cooperativa.votacao.api.dto;

import br.com.cooperativa.votacao.domain.VoteOption;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterVoteRequest(
        @NotBlank @Size(max = 64) String associateId,
        @NotBlank @Pattern(regexp = "\\d{11}", message = "cpf must contain exactly 11 digits") String cpf,
        @NotNull VoteOption vote
) {
}
