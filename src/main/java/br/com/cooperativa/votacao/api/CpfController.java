package br.com.cooperativa.votacao.api;

import br.com.cooperativa.votacao.api.dto.CpfStatusResponse;
import br.com.cooperativa.votacao.service.CpfVotingStatus;
import br.com.cooperativa.votacao.service.ExternalCpfClient;
import br.com.cooperativa.votacao.service.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cpf")
public class CpfController {

    private final ExternalCpfClient cpfClient;

    public CpfController(ExternalCpfClient cpfClient) {
        this.cpfClient = cpfClient;
    }

    @Operation(summary = "Validar CPF")
    @GetMapping("/{cpf}")
    public CpfStatusResponse check(@PathVariable String cpf) {
        CpfVotingStatus status = cpfClient.check(cpf);
        if (status == CpfVotingStatus.INVALID) {
            throw new ResourceNotFoundException("CPF não encontrado");
        }
        return new CpfStatusResponse(status.name());
    }
}
