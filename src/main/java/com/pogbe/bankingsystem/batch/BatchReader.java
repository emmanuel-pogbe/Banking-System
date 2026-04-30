package com.pogbe.bankingsystem.batch;

import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@StepScope
public class BatchReader implements ItemReader<String> {
    @Value("#{jobParameters['dto']}")
    private String dto;

    List<String> numbers;
    private int index = 0;
    @Override
    public @Nullable String read() throws Exception {
        numbers = List.of("one", "two", "three", "four", "five",dto);
        return index < numbers.size() ? numbers.get(index++) : null;
    }
}
