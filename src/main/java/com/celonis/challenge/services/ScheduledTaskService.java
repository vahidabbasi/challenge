package com.celonis.challenge.services;


import com.celonis.challenge.model.ProjectGenerationTaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ScheduledTaskService {

    private final ProjectGenerationTaskRepository projectGenerationTaskRepository;

    private LocalDateTime taskShouldBeRemovedOlderThan;

    public ScheduledTaskService(ProjectGenerationTaskRepository projectGenerationTaskRepository) {
        this.projectGenerationTaskRepository = projectGenerationTaskRepository;
    }

    @Scheduled(cron = "${scheduled.cron}")
    @Async
    public void cleanUpTasks() {
        int numberOfDeletedTasks = projectGenerationTaskRepository.updateTaskStatus(taskShouldBeRemovedOlderThan);
        log.info(numberOfDeletedTasks + " old tasks removed");
    }


    @Value("${taskShouldBeRemovedOlderThan}")
    private void setLocalDate(String localDateStr) {
        if (localDateStr != null && !localDateStr.isEmpty()) {
            taskShouldBeRemovedOlderThan = LocalDateTime.parse(localDateStr);
        }
    }
}
