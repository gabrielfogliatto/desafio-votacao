package br.com.cooperativa.votacao.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class FakeCpfClient implements ExternalCpfClient {

    private final SecureRandom random = new SecureRandom();

    @Override
    public CpfVotingStatus check(String cpf) {
        if (!isCpfShapeValid(cpf) || random.nextInt(10) == 0) {
            return CpfVotingStatus.INVALID;
        }
        return random.nextBoolean() ? CpfVotingStatus.ABLE_TO_VOTE : CpfVotingStatus.UNABLE_TO_VOTE;
    }

    private boolean isCpfShapeValid(String cpf) {
        return cpf != null && cpf.matches("\\d{11}") && !cpf.chars().allMatch(ch -> ch == cpf.charAt(0));
    }
}
