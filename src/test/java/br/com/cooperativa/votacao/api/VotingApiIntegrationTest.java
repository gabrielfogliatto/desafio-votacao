package br.com.cooperativa.votacao.api;

import br.com.cooperativa.votacao.service.CpfVotingStatus;
import br.com.cooperativa.votacao.service.ExternalCpfClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VotingApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExternalCpfClient cpfClient;

    @Test
    void shouldCreateAgendaOpenSessionVoteAndReturnResult() throws Exception {
        when(cpfClient.check(anyString())).thenReturn(CpfVotingStatus.ABLE_TO_VOTE);

        long agendaId = createAgenda("Assembleia ordinaria");
        openSession(agendaId, 60);

        mockMvc.perform(post("/api/v1/agendas/{agendaId}/votes", agendaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "associateId": "assoc-1",
                                  "cpf": "39053344705",
                                  "vote": "SIM"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.agendaId", is((int) agendaId)))
                .andExpect(jsonPath("$.vote", is("SIM")));

        mockMvc.perform(get("/api/v1/agendas/{agendaId}/result", agendaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sim", is(1)))
                .andExpect(jsonPath("$.nao", is(0)))
                .andExpect(jsonPath("$.total", is(1)))
                .andExpect(jsonPath("$.winner", is("SIM")));
    }

    @Test
    void shouldRejectDuplicatedVoteForSameAgendaAndAssociate() throws Exception {
        when(cpfClient.check(anyString())).thenReturn(CpfVotingStatus.ABLE_TO_VOTE);

        long agendaId = createAgenda("Duplicidade");
        openSession(agendaId, 60);

        String body = """
                {
                  "associateId": "assoc-duplicado",
                  "cpf": "39053344705",
                  "vote": "NAO"
                }
                """;

        mockMvc.perform(post("/api/v1/agendas/{agendaId}/votes", agendaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/agendas/{agendaId}/votes", agendaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Associate already voted on this agenda")));
    }

    @Test
    void shouldRejectVoteWhenSessionIsClosed() throws Exception {
        when(cpfClient.check(anyString())).thenReturn(CpfVotingStatus.ABLE_TO_VOTE);

        long agendaId = createAgenda("Sessao curta");
        openSession(agendaId, 1);
        Thread.sleep(1100);

        mockMvc.perform(post("/api/v1/agendas/{agendaId}/votes", agendaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "associateId": "assoc-2",
                                  "cpf": "39053344705",
                                  "vote": "SIM"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Voting session is closed")));
    }

    @Test
    void shouldReturnNotFoundWhenCpfIsUnableToVote() throws Exception {
        when(cpfClient.check(anyString())).thenReturn(CpfVotingStatus.UNABLE_TO_VOTE);

        long agendaId = createAgenda("CPF bloqueado");
        openSession(agendaId, 60);

        mockMvc.perform(post("/api/v1/agendas/{agendaId}/votes", agendaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "associateId": "assoc-3",
                                  "cpf": "39053344705",
                                  "vote": "SIM"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    private long createAgenda(String title) throws Exception {
        String location = mockMvc.perform(post("/api/v1/agendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "%s",
                                  "description": "Descricao da pauta"
                                }
                                """.formatted(title)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return Long.parseLong(location.replaceAll(".*\\\"id\\\":(\\d+).*", "$1"));
    }

    private void openSession(long agendaId, int durationSeconds) throws Exception {
        mockMvc.perform(post("/api/v1/agendas/{agendaId}/sessions", agendaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "durationSeconds": %d
                                }
                                """.formatted(durationSeconds)))
                .andExpect(status().isCreated());
    }
}
