package org.example.springbatch.batch;

import org.apache.poi.ss.usermodel.Row;
import org.example.springbatch.batch.excel.ExcelRowReader;
import org.example.springbatch.entity.AfterEntity;
import org.example.springbatch.repository.AfterRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;

@Configuration
public class FourthBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final AfterRepository afterRepository;

    public FourthBatch(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, AfterRepository afterRepository) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.afterRepository = afterRepository;
    }

    @Bean
    public Job fourthJob() {

        System.out.println("fourth job");

        return new JobBuilder("fourthJob", jobRepository)
                .start(fourthStep())
                .build();
    }

    @Bean
    public Step fourthStep() {

        return new StepBuilder("fourthStep", jobRepository)
                .<Row, AfterEntity> chunk(10, platformTransactionManager)
                .reader(excelReader())
                .processor(fourthProcessor())
                .writer(fourthAfterWriter())
                .build();
    }

    @Bean
    public ItemStreamReader<Row> excelReader() {

        try {
            return new ExcelRowReader("/Users/yeongmujo/Desktop/2024년도 정기 기사 제3회 실기시험(필답형) 시험장 현황(9.9 기준).xlsx");
            //리눅스나 맥은 /User/형태로
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public ItemProcessor<Row, AfterEntity> fourthProcessor() {

        return new ItemProcessor<Row, AfterEntity>() {

            @Override
            public AfterEntity process(Row item) {

                AfterEntity afterEntity = new AfterEntity();
//                afterEntity.setUsername(item.getCell(0).getStringCellValue());
                afterEntity.setUsername(item.getCell(2).getStringCellValue());

                return afterEntity;
            }
        };
    }

    @Bean
    public RepositoryItemWriter<AfterEntity> fourthAfterWriter() {

        return new RepositoryItemWriterBuilder<AfterEntity>()
                .repository(afterRepository)
                .methodName("save")
                .build();
    }
}
