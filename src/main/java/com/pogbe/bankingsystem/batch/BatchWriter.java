package com.pogbe.bankingsystem.batch;

import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class BatchWriter implements ItemWriter<String> {
    @Override
    public void write(Chunk<? extends String> chunk) throws Exception {
        chunk.forEach(System.out::println);
        System.out.println("=====================");
    }
}
