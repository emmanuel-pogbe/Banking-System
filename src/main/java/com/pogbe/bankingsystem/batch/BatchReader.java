package com.pogbe.bankingsystem.batch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pogbe.bankingsystem.dto.requests.TransferMoneyDTO;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pogbe.bankingsystem.dto.requests.BulkTransferRequestDTO;

@Component
@StepScope
public class BatchReader implements ItemReader<TransferMoneyDTO> {
    private final BulkTransferRequestDTO bulkTransferRequestDTO;
    private int curPosition = 0;
    private final int finalNo;

    public BatchReader(@Value("#{jobParameters['dto']}") String dto) throws JsonProcessingException {
        if (dto == null || dto.isBlank()) {
            throw new IllegalArgumentException("Job parameter 'dto' is required for bulk transfer");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        this.bulkTransferRequestDTO = objectMapper.readValue(dto, BulkTransferRequestDTO.class);
        this.finalNo = bulkTransferRequestDTO.getTransfers().size();
    }

    @Override
    public @Nullable TransferMoneyDTO read() throws Exception {
        curPosition++;
        return curPosition > finalNo ? null : bulkTransferRequestDTO.getTransfers().get(curPosition - 1);
    }
}
