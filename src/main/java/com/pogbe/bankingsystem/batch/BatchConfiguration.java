package com.pogbe.bankingsystem.batch;

import com.pogbe.bankingsystem.dto.requests.TransferMoneyDTO;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class BatchConfiguration {
    private final BatchReader batchReader;
    private final BatchProcessor batchProcessor;
    private final BatchWriter batchWriter;

    private final JobRepository jobRepository;

    public BatchConfiguration(BatchReader batchReader, BatchProcessor batchProcessor, BatchWriter batchWriter, JobRepository jobRepository) {
        this.batchReader = batchReader;
        this.batchProcessor = batchProcessor;
        this.batchWriter = batchWriter;
        this.jobRepository = jobRepository;
    }

    @Bean
    public Step step() {
        return new StepBuilder("step", jobRepository)
                .<TransferMoneyDTO, TransferMoneyDTO>chunk(1)
                .reader(batchReader)
                .processor(batchProcessor)
                .writer(batchWriter)
                .build();
    }

    @Bean
    public Job job(Step step) {
        return new JobBuilder("job", jobRepository)
                .start(step)
                .build();
    }
}
