package br.com.walter.contaclara;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.YearMonth;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FinanceApiIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private JdbcTemplate jdbc;

    @BeforeEach
    void cleanDatabase() {
        jdbc.update("DELETE FROM transaction_drafts");
        jdbc.update("DELETE FROM monthly_budgets");
        jdbc.update("DELETE FROM finance_transactions");
    }

    @Test
    void confirmsExpenseOnceEvenWhenConfirmationIsRetried() throws Exception {
        String draftBody = """
                {"description":"Mercado","amount":82.50,"type":"EXPENSE","category":"FOOD"}
                """;

        String response = mockMvc.perform(post("/api/drafts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(draftBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.confirmed", is(false)))
                .andReturn().getResponse().getContentAsString();

        String draftId = com.jayway.jsonpath.JsonPath.read(response, "$.id");

        String first = mockMvc.perform(post("/api/drafts/{id}/confirm", draftId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type", is("EXPENSE")))
                .andReturn().getResponse().getContentAsString();
        String firstTransactionId = com.jayway.jsonpath.JsonPath.read(first, "$.id");

        mockMvc.perform(post("/api/drafts/{id}/confirm", draftId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstTransactionId)));

        org.junit.jupiter.api.Assertions.assertEquals(
                1, jdbc.queryForObject("SELECT COUNT(*) FROM finance_transactions", Integer.class));
    }

    @Test
    void summaryShowsBudgetAlertAtEightyPercent() throws Exception {
        String month = YearMonth.now().toString();
        String draft = mockMvc.perform(post("/api/drafts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description":"Compra semanal","amount":80.00,"type":"EXPENSE","category":"FOOD"}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String draftId = com.jayway.jsonpath.JsonPath.read(draft, "$.id");
        mockMvc.perform(post("/api/drafts/{id}/confirm", draftId)).andExpect(status().isOk());

        mockMvc.perform(put("/api/budgets/{month}/FOOD", month)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"limit\":100.00}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/summary/{month}", month))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expenses", is(80.0)))
                .andExpect(jsonPath("$.categories[?(@.category == 'FOOD')].status[0]", is("NEAR_LIMIT")));
    }

    @Test
    void rejectsZeroAmountBeforePersisting() throws Exception {
        mockMvc.perform(post("/api/drafts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description":"Café","amount":0,"type":"EXPENSE","category":"FOOD"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("inválidos")));

        org.junit.jupiter.api.Assertions.assertEquals(
                0, jdbc.queryForObject("SELECT COUNT(*) FROM transaction_drafts", Integer.class));
    }

    @Test
    void listsTransactionsOnlyForRequestedMonth() throws Exception {
        String draft = mockMvc.perform(post("/api/drafts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description":"Passagem","amount":12.40,"type":"EXPENSE","category":"TRANSPORT"}
                                """))
                .andReturn().getResponse().getContentAsString();
        UUID draftId = UUID.fromString(com.jayway.jsonpath.JsonPath.read(draft, "$.id"));
        mockMvc.perform(post("/api/drafts/{id}/confirm", draftId)).andExpect(status().isOk());

        mockMvc.perform(get("/api/transactions").param("month", YearMonth.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()", is(1)))
                .andExpect(jsonPath("$.items[0].description", is("Passagem")));

        mockMvc.perform(get("/api/transactions").param("month", "2020-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()", is(0)));
    }

    @Test
    void rejectsUnsupportedAudioExtensionWithoutCallingProvider() throws Exception {
        mockMvc.perform(multipart("/api/assistant/voice")
                        .file("file", "not audio".getBytes())
                        .param("includeSpeech", "false"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Formato não suportado")));
    }
}
