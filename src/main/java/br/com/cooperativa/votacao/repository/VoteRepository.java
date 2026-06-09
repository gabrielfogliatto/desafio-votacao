package br.com.cooperativa.votacao.repository;

import br.com.cooperativa.votacao.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByAgendaIdAndAssociateId(Long agendaId, String associateId);

    @Query("""
            select v.option as option, count(v.id) as total
            from Vote v
            where v.agenda.id = :agendaId
            group by v.option
            """)
    List<VoteCountProjection> countVotesByAgenda(@Param("agendaId") Long agendaId);
}
