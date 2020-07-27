package org.tinywind.server.task;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

// thread pool 테스트 용도
@Slf4j
@Configuration
public class DummyTask {

    @Bean("job1")
    public Job job1() {
        return new Job();
    }

    @Bean("job2")
    public Job job2() {
        return new Job();
    }

    static class Job {
        @SneakyThrows
        @Scheduled(fixedRate = 1000 * 10) // 10초마다 실행
        public void task() {
            log.info(Thread.currentThread().getId() + "-" + Thread.currentThread().getName() + ": sleep(2min)");
            Thread.sleep(1000 * 60 * 2);
        }
    }
}
