package com.aflr.spring_batch.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.aflr.spring_batch.constants.BatchConstants.JobParametersConstants.FILE_INPUT_KEY_NAME;
import static com.aflr.spring_batch.constants.BatchConstants.JobParametersConstants.START_AT_KEY_NAME;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    public TransactionController(JobLauncher jobLauncher, Job transactionLoadJob) {
        this.jobLauncher = jobLauncher;
        this.transactionLoadJob = transactionLoadJob;
    }

    private final JobLauncher jobLauncher;
    private final Job transactionLoadJob;

    @PostMapping
    public void trigger() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong(START_AT_KEY_NAME, System.currentTimeMillis())
                .addString(FILE_INPUT_KEY_NAME, "transactions.csv")
                .toJobParameters();

        try {
            jobLauncher.run(transactionLoadJob, jobParameters);
        } catch (JobExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
