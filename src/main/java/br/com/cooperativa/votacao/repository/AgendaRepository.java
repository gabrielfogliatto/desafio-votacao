package br.com.cooperativa.votacao.repository;

import br.com.cooperativa.votacao.domain.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {
}
