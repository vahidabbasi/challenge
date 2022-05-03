package com.celonis.challenge.services;

import com.celonis.challenge.model.ProjectGenerationTaskRepository;
import com.celonis.challenge.utils.ProjectGenerationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class CounterService {

    private final ProjectGenerationUtil projectGenerationUtil;

    private final ProjectGenerationTaskRepository projectGenerationTaskRepository;

    public CounterService(ProjectGenerationUtil projectGenerationUtil,
                          ProjectGenerationTaskRepository projectGenerationTaskRepository
    ) {
        this.projectGenerationUtil = projectGenerationUtil;
        this.projectGenerationTaskRepository = projectGenerationTaskRepository;

    }

    public void interrupt(String taskId) {
        Long threadId = projectGenerationUtil.get(taskId).getThreadId();

        Set<Thread> threads = Thread.getAllStackTraces().keySet();
        for (Thread t : threads) {
            if (t.getId() == threadId) {
                t.interrupt();
                log.info("Thread Id: {} is interrupted", threadId);
                break;
            }
        }
    }

    public String getCounterResultTask(String taskId) {
        return projectGenerationUtil.get(taskId).getTaskStatus().name();
    }

    public void updateProgress(String taskId, Integer duration, Integer counterValue) {
        float processedPercentage = (float) counterValue / duration;
        projectGenerationTaskRepository.updateProgress(taskId, processedPercentage);
    }
}
