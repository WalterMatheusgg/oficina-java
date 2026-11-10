package br.com.walter.designpatterns.api;

import br.com.walter.designpatterns.domain.TransactionType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getsExistingAccount() throws Exception {
        mockMvc.perform(get("/api/accounts/ACC-1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value("ACC-1001"))
                .andExpect(jsonPath("$.balance").exists());
    }

    @Test
    void returnsNotFoundForUnknownAccount() throws Exception {
        mockMvc.perform(get("/api/accounts/ACC-MISSING"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Account not found: ACC-MISSING"));
    }

    @Test
    void createsDepositAndWithdrawalAndListsTheirHistory() throws Exception {
        TransactionResponse deposit = createTransaction("ACC-1002", "DEPOSIT", "20.50");
        TransactionResponse withdrawal = createTransaction("ACC-1002", "WITHDRAWAL", "5.00");

        MvcResult result = mockMvc.perform(get("/api/accounts/ACC-1002/transactions"))
                .andExpect(status().isOk())
                .andReturn();

        List<TransactionResponse> history = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() { }
        );

        assertThat(history).hasSizeGreaterThanOrEqualTo(2);
        assertThat(history)
                .extracting(TransactionResponse::transactionId)
                .contains(deposit.transactionId(), withdrawal.transactionId());
        assertThat(history)
                .extracting(TransactionResponse::type)
                .contains(TransactionType.DEPOSIT, TransactionType.WITHDRAWAL);
    }

    @Test
    void returnsBadRequestWhenWithdrawalExceedsBalance() throws Exception {
        mockMvc.perform(post("/api/accounts/ACC-1001/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"type":"WITHDRAWAL","amount":999999999.00}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insufficient funds."));
    }

    @Test
    void returnsBadRequestForNonPositiveAmount() throws Exception {
        mockMvc.perform(post("/api/accounts/ACC-1001/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"type":"DEPOSIT","amount":0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Amount must be greater than zero."));
    }

    private TransactionResponse createTransaction(String accountId, String type, String amount)
            throws Exception {
        MvcResult result = mockMvc.perform(post("/api/accounts/{accountId}/transactions", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"type":"%s","amount":%s}
                                """.formatted(type, amount)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").exists())
                .andExpect(jsonPath("$.accountId").value(accountId))
                .andExpect(jsonPath("$.amount").value(Double.parseDouble(amount)))
                .andExpect(jsonPath("$.balanceAfter").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andReturn();

        return objectMapper.readValue(
                result.getResponse().getContentAsString(),
                TransactionResponse.class
        );
    }
}
