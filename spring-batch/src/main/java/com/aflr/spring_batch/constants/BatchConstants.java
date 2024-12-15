package com.aflr.spring_batch.constants;

public final class BatchConstants {
    public static final String TRANSACTION_LOAD_JOB_NAME = "transactionLoadJob";
    public static final String TRANSACTION_LOAD_STEP_NAME = "transactionLoadStep";


    public static class JobParametersConstants {
        public static final String FILE_INPUT_KEY_NAME = "file.input";
        public static final String START_AT_KEY_NAME = "startAt";
    }

    private BatchConstants() {
    }
}
