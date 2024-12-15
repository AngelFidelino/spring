package com.aflr.spring_batch.config;

import com.aflr.spring_batch.domains.Transaction;
import com.aflr.spring_batch.dtos.TransactionDto;
import com.aflr.spring_batch.repositories.TransactionRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static com.aflr.spring_batch.constants.BatchConstants.JobParametersConstants.FILE_INPUT_KEY_NAME;
import static com.aflr.spring_batch.constants.BatchConstants.TRANSACTION_LOAD_JOB_NAME;
import static com.aflr.spring_batch.constants.BatchConstants.TRANSACTION_LOAD_STEP_NAME;

@Configuration
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final TransactionRepository transactionRepository;

    public BatchConfig(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager,
            TransactionRepository transactionRepository) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.transactionRepository = transactionRepository;
    }

    @Bean
    @JobScope
    public FlatFileItemReader<TransactionDto> itemReader(
            @Value("#{jobParameters['" + FILE_INPUT_KEY_NAME + "']}") String inputFile) {
        return new FlatFileItemReaderBuilder()
                .name("CSVReader")
                .resource(new ClassPathResource(inputFile))
                .lineMapper(buildMapper())
                .linesToSkip(1)
                .build();
    }

    private LineMapper<TransactionDto> buildMapper() {
        DefaultLineMapper<TransactionDto> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("transactionIdSource", "clientId", "amount", "transactionDate");

        BeanWrapperFieldSetMapper<TransactionDto> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(TransactionDto.class);
        fieldSetMapper.setConversionService(testConversionService());

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    @Bean
    public TransactionProcessor transactionProcessor() {
        return new TransactionProcessor();
    }

    @Bean
    public RepositoryItemWriter<Transaction> itemWriter() {
        RepositoryItemWriter<Transaction> itemWriter = new RepositoryItemWriter<>();
        itemWriter.setRepository(transactionRepository);
        itemWriter.setMethodName("save");
        return itemWriter;
    }

    @Bean("batchLoadStep")
    public Step batchLoadStep(FlatFileItemReader itemReader, TransactionProcessor itemProcessor,
            RepositoryItemWriter itemWriter) {
        return new StepBuilder(TRANSACTION_LOAD_STEP_NAME, jobRepository)
                .<TransactionDto, Transaction>chunk(100, platformTransactionManager)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .listener(new TxnItemListener())
                .build();
    }

    @Bean
    public Job job(@Qualifier("batchLoadStep") Step batchLoadStep) {
        return new JobBuilder(TRANSACTION_LOAD_JOB_NAME, jobRepository)
                .start(batchLoadStep)
                .build();
    }

    public ConversionService testConversionService() {
        DefaultConversionService testConversionService = new DefaultConversionService();
        DefaultConversionService.addDefaultConverters(testConversionService);
        testConversionService.addConverter(new Converter<String, ZonedDateTime>() {
            @Override
            public ZonedDateTime convert(String text) {
                return ZonedDateTime.parse(text, DateTimeFormatter.ISO_ZONED_DATE_TIME);
            }
        });

        return testConversionService;
    }
}
