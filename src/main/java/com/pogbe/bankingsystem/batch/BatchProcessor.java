package com.pogbe.bankingsystem.batch;

import com.pogbe.bankingsystem.dto.requests.TransferMoneyDTO;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class BatchProcessor implements ItemProcessor<TransferMoneyDTO, TransferMoneyDTO> {
    @Override
    public @Nullable TransferMoneyDTO process(TransferMoneyDTO item) throws Exception {
        return item;
    }
}
