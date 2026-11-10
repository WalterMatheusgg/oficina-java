package br.com.walter.designpatterns.api;

import br.com.walter.designpatterns.domain.TransactionType;
import br.com.walter.designpatterns.persistence.AccountEntity;
import br.com.walter.designpatterns.persistence.AccountJpaRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

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

    @Autowired
    private AccountJpaRepository accounts;

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
    void persistsBalanceAndHistoryTogether() throws Exception {
        String accountId = createAccount("250.00");
        TransactionResponse deposit = createTransaction(accountId, "DEPOSIT", "20.50");
        TransactionResponse withdrawal = createTransaction(accountId, "WITHDRAWAL", "5.00");

        MvcResult accountResult = mockMvc.perform(get("/api/accounts/{accountId}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(265.50))
                .andReturn();
        MvcResult historyResult = mockMvc.perform(get("/api/accounts/{accountId}/transactions", accountId))
                .andExpect(status().isOk())
                .andReturn();

        List<TransactionResponse> history = objectMapper.readValue(
                historyResult.getResponse().getContentAsString(),
                new TypeReference<>() { }
        );
        AccountResponse account = objectMapper.readValue(
                accountResult.getResponse().getContentAsString(),
                AccountResponse.class
        );

        assertThat(account.balance()).isEqualByComparingTo("265.50");
        assertThat(history).hasSize(2);
        assertThat(history)
                .extracting(TransactionResponse::transactionId)
                .contains(deposit.transactionId(), withdrawal.transactionId());
        assertThat(history)
                .extracting(TransactionResponse::type)
                .contains(TransactionType.DEPOSIT, TransactionType.WITHDRAWAL);
    }

    @Test
    void replayingSameIdempotencyKeyDoesNotApplyDepositTwice() throws Exception {
        String accountId = createAccount("100.00");
        String key = "retry-" + UUID.randomUUID();

        TransactionResponse first = performTransaction(accountId, key, "DEPOSIT", "20.00");
        TransactionResponse replay = performTransaction(accountId, key, "DEPOSIT", "20.00");

        assertThat(replay.transactionId()).isEqualTo(first.transactionId());
        assertThat(replay.balanceAfter()).isEqualByComparingTo("120.00");
        mockMvc.perform(get("/api/accounts/{accountId}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(120.00));
        mockMvc.perform(get("/api/accounts/{accountId}/transactions", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)));
    }

    @Test
    void rejectsReusingIdempotencyKeyWithDifferentRequest() throws Exception {
        String accountId = createAccount("100.00");
        String key = "conflict-" + UUID.randomUUID();
        performTransaction(accountId, key, "DEPOSIT", "10.00");

        mockMvc.perform(post("/api/accounts/{accountId}/transactions", accountId)
                        .header("Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"type":"DEPOSIT","amount":11.00}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        "This Idempotency-Key was already used with a different transaction."
                ));

        mockMvc.perform(get("/api/accounts/{accountId}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(110.00));
        mockMvc.perform(get("/api/accounts/{accountId}/transactions", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)));
    }

    @Test
    void returnsBadRequestWhenWithdrawalExceedsBalance() throws Exception {
        String accountId = createAccount("10.00");
        mockMvc.perform(post("/api/accounts/{accountId}/transactions", accountId)
                        .header("Idempotency-Key", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"type":"WITHDRAWAL","amount":999999999.00}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insufficient funds."));
    }

    @Test
    void returnsBadRequestForNonPositiveAmount() throws Exception {
        String accountId = createAccount("10.00");
        mockMvc.perform(post("/api/accounts/{accountId}/transactions", accountId)
                        .header("Idempotency-Key", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"type":"DEPOSIT","amount":0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Amount must be greater than zero."));
    }

    @Test
    void rejectsAmountsWithFractionalCents() throws Exception {
        String accountId = createAccount("10.00");
        mockMvc.perform(post("/api/accounts/{accountId}/transactions", accountId)
                        .header("Idempotency-Key", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"type":"DEPOSIT","amount":1.001}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Amount cannot have more than two decimal places."));
    }

    @Test
    void requiresIdempotencyKey() throws Exception {
        String accountId = createAccount("10.00");
        mockMvc.perform(post("/api/accounts/{accountId}/transactions", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"type":"DEPOSIT","amount":1.00}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Required header is missing: Idempotency-Key."));
    }

    private String createAccount(String openingBalance) {
        String accountId = "ACC-TEST-" + UUID.randomUUID();
        accounts.save(new AccountEntity(accountId, new BigDecimal(openingBalance)));
        return accountId;
    }

    private TransactionResponse createTransaction(String accountId, String type, String amount)
            throws Exception {
        return performTransaction(accountId, UUID.randomUUID().toString(), type, amount);
    }

    private TransactionResponse performTransaction(String accountId, String key, String type, String amount)
            throws Exception {
        MvcResult result = mockMvc.perform(post("/api/accounts/{accountId}/transactions", accountId)
                        .header("Idempotency-Key", key)
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
