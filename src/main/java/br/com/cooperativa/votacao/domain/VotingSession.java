package br.com.cooperativa.votacao.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "voting_sessions")
public class VotingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agenda_id", nullable = false, unique = true)
    private Agenda agenda;

    @Column(nullable = false, updatable = false)
    private Instant openedAt;

    @Column(nullable = false, updatable = false)
    private Instant closesAt;

    protected VotingSession() {
    }

    public VotingSession(Agenda agenda, Instant openedAt, Instant closesAt) {
        this.agenda = agenda;
        this.openedAt = openedAt;
        this.closesAt = closesAt;
    }

    public Long getId() {
        return id;
    }

    public Agenda getAgenda() {
        return agenda;
    }

    public Instant getOpenedAt() {
        return openedAt;
    }

    public Instant getClosesAt() {
        return closesAt;
    }

    public VotingStatus statusAt(Instant instant) {
        return instant.isBefore(closesAt) ? VotingStatus.OPEN : VotingStatus.CLOSED;
    }
}
