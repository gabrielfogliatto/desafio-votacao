package br.com.cooperativa.votacao.api;

import br.com.cooperativa.votacao.api.dto.AgendaResponse;
import br.com.cooperativa.votacao.api.dto.CreateAgendaRequest;
import br.com.cooperativa.votacao.service.AgendaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agendas")
public class AgendaController {

    private final AgendaService agendaService;

    public AgendaController(AgendaService agendaService) {
        this.agendaService = agendaService;
    }

    @Operation(summary = "Criar nova pauta")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgendaResponse create(@Valid @RequestBody CreateAgendaRequest request) {
        return agendaService.create(request);
    }

    @Operation(summary = "Listar pautas")
    @GetMapping
    public List<AgendaResponse> list() {
        return agendaService.list();
    }
}
