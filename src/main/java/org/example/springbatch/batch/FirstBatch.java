package org.example.springbatch.batch;

import org.example.springbatch.entity.AfterEntity;
import org.example.springbatch.entity.BeforeEntity;
import org.example.springbatch.repository.AfterRepository;
import org.example.springbatch.repository.BeforeRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@Configuration
public class FirstBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final BeforeRepository beforeRepository;
    private final AfterRepository afterRepository;

    public FirstBatch(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, BeforeRepository beforeRepository, AfterRepository afterRepository) {

        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.beforeRepository = beforeRepository;
        this.afterRepository = afterRepository;
    }

    /**
     * JOB
     */
    @Bean
    public Job firstJob() {

        System.out.println("first job");

        return new JobBuilder("firstJob", jobRepository) // job의 이름 지정, 메서드 명과 동일하게 지정 했습니다.
                .start(firstStep()) // 스탭이 들어갈 자리입니다.
//                .next() 여러가지 스탭을 지정할 수 있습니다.
                .build();
    }

    /**
     * STEP
     */
    @Bean
    public Step firstStep() {

        System.out.println("first step");

        return new StepBuilder("firstStep", jobRepository) // 스탭 이름 지정
                .<BeforeEntity, AfterEntity> chunk(10, platformTransactionManager) // 끊어서 읽을 최소 단위 입니다.
                // 너무 작으면 I/O 처리가 많아지고 오버헤드 발생, 너무 크면 적재 및 자원 사용에 대한 비용과 실패시 부담이 커집니다.
                .reader(beforeReader()) // 읽는 메소드 자리 입니다.
                .processor(middleProcessor()) // 처리 메소드 자리 입니다.
                .writer(afterWriter()) // 쓰기 메소드 자리입니다.
                .build();
    }

    /**
     * READ
     */
    @Bean
    public RepositoryItemReader<BeforeEntity> beforeReader() {

        return new RepositoryItemReaderBuilder<BeforeEntity>()
                .name("beforeReader")
                .pageSize(10) // 페이징 처리 입니다.
                .methodName("findAll") // jpa 메소드 입니다.
                .repository(beforeRepository) // findAll 쿼리가 실행될 repository 입니다.
                .sorts(Map.of("id", Sort.Direction.ASC)) // 페이징 sort 입니다.
                .build();
    }

    /**
     * PROCESS : 읽어온 데이터를 처리하는 Process (큰 작업을 수행하지 않을 경우 생략 가능, 지금과 같이 단순 이동은 사실 필요 없음)
     */
    @Bean
    public ItemProcessor<BeforeEntity, AfterEntity> middleProcessor() {

        return new ItemProcessor<BeforeEntity, AfterEntity>() { // 익명 클래스가 아닌 람다로 변경 가능합니다.

            @Override
            public AfterEntity process(BeforeEntity item) throws Exception { // read로 읽은 데이터를 받습니다.

                AfterEntity afterEntity = new AfterEntity();
                afterEntity.setUsername(item.getUsername());

                return afterEntity; // write 에 넘길 데이터 입니다.
            }
        };
    }

    /**
     * Writer
     */
    @Bean
    public RepositoryItemWriter<AfterEntity> afterWriter() {

        return new RepositoryItemWriterBuilder<AfterEntity>()
                .repository(afterRepository) // 저장할 Entity Repository를 지정합니다.
                .methodName("save") // JPA 저장 메서드
                .build();
    }
}
