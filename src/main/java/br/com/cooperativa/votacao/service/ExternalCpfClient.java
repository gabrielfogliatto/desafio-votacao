package br.com.cooperativa.votacao.service;

public interface ExternalCpfClient {

    CpfVotingStatus check(String cpf);
}
