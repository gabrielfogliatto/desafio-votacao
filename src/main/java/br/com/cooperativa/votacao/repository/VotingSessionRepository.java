package br.com.cooperativa.votacao.repository;

import br.com.cooperativa.votacao.domain.VotingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VotingSessionRepository extends JpaRepository<VotingSession, Long> {

    Optional<VotingSession> findByAgendaId(Long agendaId);

    boolean existsByAgendaId(Long agendaId);
}
