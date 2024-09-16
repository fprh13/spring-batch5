package org.example.springbatch.controller;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class MainController {

    private final JobLauncher jobLauncher; // job 을 시작하기 위한 시작 시점
    private final JobRegistry jobRegistry; // 빈으로 등록한 특정 job 을 가지고 오기위한 레지스트리 입니다. (firstJob)

    public MainController(JobLauncher jobLauncher, JobRegistry jobRegistry) {
        this.jobLauncher = jobLauncher;
        this.jobRegistry = jobRegistry;
    }

    @GetMapping("/first")
    public String firstApi(@RequestParam("value") String value) throws Exception {
        // 배치 작업이 오래 걸리기 때문에 callable 로 비동기 처리를 진행 해야됩니다.
        // 해당 프로젝트에는 동기 방식으로 진행 합니다.
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", value)
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("firstJob"), jobParameters); // 특정 일자에 실행될 수 있도록 통제 하기 위함 입니다.


        return "ok";
    }
}
