package com.pogbe.bankingsystem.batch;

import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class BatchProcessor implements ItemProcessor<String, String> {
    @Override
    public @Nullable String process(String item) throws Exception {
        return item.toUpperCase();
    }
}
