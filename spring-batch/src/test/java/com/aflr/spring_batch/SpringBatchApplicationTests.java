package com.aflr.spring_batch;

import com.aflr.spring_batch.config.BatchConfig;
import com.aflr.spring_batch.domains.Transaction;
import com.aflr.spring_batch.repositories.TransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static com.aflr.spring_batch.constants.BatchConstants.JobParametersConstants.FILE_INPUT_KEY_NAME;
import static com.aflr.spring_batch.constants.BatchConstants.JobParametersConstants.START_AT_KEY_NAME;
import static com.aflr.spring_batch.constants.BatchConstants.TRANSACTION_LOAD_JOB_NAME;
import static com.aflr.spring_batch.constants.BatchConstants.TRANSACTION_LOAD_STEP_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableAutoConfiguration
@SpringBatchTest
@SpringJUnitConfig(BatchConfig.class)
public class SpringBatchApplicationTests {
    private static final int TEST_FILE_RECORD_NUMBER = 3;
    public static final String INVALID_TRANSACTIONS_TEST_CSV = "invalid-transactions-test.csv";
    public static final String VALID_TRANSACTIONS_TEST_CSV = "transactions-test.csv";

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;
    @Autowired
    private TransactionRepository transactionRepository;

    @AfterEach
    public void afterEach() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    private JobParameters buildJobParameters() {
        return new JobParametersBuilder()
                .addLong(START_AT_KEY_NAME, System.currentTimeMillis())
                .addString(FILE_INPUT_KEY_NAME, VALID_TRANSACTIONS_TEST_CSV)
                .toJobParameters();
    }

    @Test
    public void testJob() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(buildJobParameters());
        Iterable<Transaction> transactions = transactionRepository.findAll();

        assertEquals(ExitStatus.COMPLETED.getExitCode(), jobExecution.getExitStatus().getExitCode());
        assertEquals(TRANSACTION_LOAD_JOB_NAME, jobExecution.getJobInstance().getJobName());
        System.out.println("jobExecution.getJobInstance().getJobName(): " + jobExecution.getJobInstance().getJobName());
        assertThat(transactions).hasSize(TEST_FILE_RECORD_NUMBER);
    }

    @Test
    public void testStep() {
        JobExecution jobExecution = jobLauncherTestUtils.launchStep(TRANSACTION_LOAD_STEP_NAME, buildJobParameters());

        assertEquals(1, jobExecution.getStepExecutions().size());
        assertEquals(ExitStatus.COMPLETED.getExitCode(), jobExecution.getExitStatus().getExitCode());
    }

    @Test
    public void givenInvalidFile_whenExecutingStep_thenFail() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString(FILE_INPUT_KEY_NAME, INVALID_TRANSACTIONS_TEST_CSV)
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchStep(TRANSACTION_LOAD_STEP_NAME, jobParameters);

        assertEquals(1, jobExecution.getStepExecutions().size());
        assertEquals(ExitStatus.FAILED.getExitCode(), jobExecution.getExitStatus().getExitCode());
    }
}
