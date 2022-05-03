package com.celonis.challenge.utils;

import com.celonis.challenge.exceptions.TaskExecutionException;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.ProjectGenerationTaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProjectGenerationUtil {

    private final ProjectGenerationTaskRepository projectGenerationTaskRepository;

    public ProjectGenerationUtil(ProjectGenerationTaskRepository projectGenerationTaskRepository) {
        this.projectGenerationTaskRepository = projectGenerationTaskRepository;
    }

    public ProjectGenerationTask get(String taskId) {
        Optional<ProjectGenerationTask> projectGenerationTask = projectGenerationTaskRepository.findById(taskId);
        return projectGenerationTask.orElseThrow(() -> new TaskExecutionException("task not found",
                HttpStatus.NOT_FOUND));
    }
}
