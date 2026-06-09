package br.com.cooperativa.votacao.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(
        name = "votes",
        uniqueConstraints = @UniqueConstraint(name = "uk_vote_agenda_associate", columnNames = {"agenda_id", "associate_id"}),
        indexes = {
                @Index(name = "idx_votes_agenda_option", columnList = "agenda_id, vote_option"),
                @Index(name = "idx_votes_associate", columnList = "associate_id")
        }
)
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agenda_id", nullable = false)
    private Agenda agenda;

    @Column(name = "associate_id", nullable = false, length = 64)
    private String associateId;

    @Column(nullable = false, length = 11)
    private String cpf;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "vote_option", length = 3)
    private VoteOption option;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Vote() {
    }

    public Vote(Agenda agenda, String associateId, String cpf, VoteOption option) {
        this.agenda = agenda;
        this.associateId = associateId;
        this.cpf = cpf;
        this.option = option;
    }

    public Long getId() {
        return id;
    }

    public Agenda getAgenda() {
        return agenda;
    }

    public String getAssociateId() {
        return associateId;
    }

    public String getCpf() {
        return cpf;
    }

    public VoteOption getOption() {
        return option;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
