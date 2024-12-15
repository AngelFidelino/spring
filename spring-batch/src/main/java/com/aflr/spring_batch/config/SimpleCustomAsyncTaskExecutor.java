package com.aflr.spring_batch.config;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.scope.context.JobSynchronizationManager;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Deprecated
public class SimpleCustomAsyncTaskExecutor extends SimpleAsyncTaskExecutor {
    @Override
    protected void doExecute(Runnable task) {
        final JobExecution jobExecution = JobSynchronizationManager.getContext().getJobExecution();
        super.doExecute(() -> JobSynchronizationManager.register(jobExecution));
        try {
            task.run();
        } finally {
            JobSynchronizationManager.release();
        }
    }
}
