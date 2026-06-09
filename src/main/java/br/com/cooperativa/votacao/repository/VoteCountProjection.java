package br.com.cooperativa.votacao.repository;

import br.com.cooperativa.votacao.domain.VoteOption;

public interface VoteCountProjection {

    VoteOption getOption();

    long getTotal();
}
