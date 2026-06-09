package br.com.cooperativa.votacao.service;

import br.com.cooperativa.votacao.api.dto.AgendaResponse;
import br.com.cooperativa.votacao.api.dto.CreateAgendaRequest;
import br.com.cooperativa.votacao.domain.Agenda;
import br.com.cooperativa.votacao.repository.AgendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AgendaService {

    private final AgendaRepository agendaRepository;

    public AgendaService(AgendaRepository agendaRepository) {
        this.agendaRepository = agendaRepository;
    }

    @Transactional
    public AgendaResponse create(CreateAgendaRequest request) {
        Agenda agenda = agendaRepository.save(new Agenda(request.title().trim(), request.description().trim()));
        return AgendaResponse.from(agenda);
    }

    @Transactional(readOnly = true)
    public List<AgendaResponse> list() {
        return agendaRepository.findAll().stream()
                .map(AgendaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Agenda getOrThrow(Long agendaId) {
        return agendaRepository.findById(agendaId)
                .orElseThrow(() -> new ResourceNotFoundException("Agenda não encontrada"));
    }
}
