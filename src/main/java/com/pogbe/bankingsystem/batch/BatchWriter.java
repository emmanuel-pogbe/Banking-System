package com.pogbe.bankingsystem.batch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pogbe.bankingsystem.dto.requests.TransferMoneyDTO;
import com.pogbe.bankingsystem.models.Account;
import com.pogbe.bankingsystem.services.interfaces.TransactionService;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.SerializationFeature;

@Component
@StepScope
public class BatchWriter implements ItemWriter<TransferMoneyDTO> {
    private final TransactionService transactionService;
    private final BulkTransferReportHolder reportHolder;

    private final Account account;
    private final String jobId;

    public BatchWriter(@Value("#{jobParameters['sender']}") String accountObject,
                       TransactionService transactionService,
                       @Value("#{jobParameters['time']}") Long time,
                       BulkTransferReportHolder reportHolder) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())

                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        this.transactionService = transactionService;
        this.reportHolder = reportHolder;
        this.account = objectMapper.readValue(accountObject, Account.class);
        this.jobId = String.valueOf(time);
    }

    @Override
    public void write(Chunk<? extends TransferMoneyDTO> chunk) throws Exception {
        for (TransferMoneyDTO item : chunk.getItems()) {
            transactionService.transferLogicWithoutPin(account, item);
            reportHolder.addTransfer(jobId, item.getAmount());
            System.out.println("Done with " + item);
        }
        System.out.println("=====================");
    }
}
