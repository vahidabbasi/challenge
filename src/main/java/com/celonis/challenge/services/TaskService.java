package com.celonis.challenge.services;

import com.celonis.challenge.exceptions.TaskExecutionException;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.ProjectGenerationTaskRepository;
import com.celonis.challenge.model.TaskStatus;
import com.celonis.challenge.model.TaskType;
import com.celonis.challenge.utils.ProjectGenerationUtil;
import com.celonis.challenge.validation.RequestValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class TaskService {

    private final ProjectGenerationTaskRepository projectGenerationTaskRepository;

    private final FileService fileService;

    private final CounterService counterService;

    private final RequestValidator requestValidator;

    private final ProjectGenerationUtil projectGenerationUtil;

    public TaskService(ProjectGenerationTaskRepository projectGenerationTaskRepository,
                       RequestValidator requestValidator,
                       CounterService counterService,
                       ProjectGenerationUtil projectGenerationUtil,
                       FileService fileService) {
        this.projectGenerationTaskRepository = projectGenerationTaskRepository;
        this.counterService = counterService;
        this.fileService = fileService;
        this.projectGenerationUtil = projectGenerationUtil;
        this.requestValidator = requestValidator;
    }

    public List<ProjectGenerationTask> listTasks() {
        return projectGenerationTaskRepository.findAll();
    }

    public ProjectGenerationTask createTask(ProjectGenerationTask projectGenerationTask) {
        requestValidator.validateTaskCreationRequest(projectGenerationTask);
        //projectGenerationTask.setId(null); //It is not needed
        projectGenerationTask.setCreationDate(LocalDateTime.now());
        return projectGenerationTaskRepository.save(projectGenerationTask);
    }


    public ProjectGenerationTask updateTask(String taskId, ProjectGenerationTask updateRequest) {
        ProjectGenerationTask projectGenerationTask;
        ProjectGenerationTask projectGenerationTaskFromDb = null;
        try {
            projectGenerationTaskFromDb = projectGenerationUtil.get(taskId);
        } catch (TaskExecutionException e) {
            log.info("task with id {} does not exist", taskId);
        }
        if (null != projectGenerationTaskFromDb) {
            requestValidator.checkTaskStatus(projectGenerationTaskFromDb.getTaskStatus());//only update the task with status
            requestValidator.validateTaskCreationRequest(updateRequest);
            projectGenerationTaskFromDb.setCreationDate(updateRequest.getCreationDate());
            projectGenerationTaskFromDb.setName(updateRequest.getName());
            projectGenerationTaskFromDb.setEndTime(updateRequest.getEndTime());
            projectGenerationTaskFromDb.setStartTime(updateRequest.getStartTime());
            projectGenerationTask = projectGenerationTaskRepository.save(projectGenerationTaskFromDb);
        } else {
            requestValidator.validateTaskCreationRequest(updateRequest);
            updateRequest.setCreationDate(LocalDateTime.now());
            updateRequest.setId(null);
            projectGenerationTask = projectGenerationTaskRepository.save(updateRequest);
        }
        return projectGenerationTask;
    }

    public void deleteTask(String taskId) {
        try {
            projectGenerationTaskRepository.deleteById(taskId);
        } catch (EmptyResultDataAccessException e) {
            throw new TaskExecutionException("task with id " + taskId + " not found", HttpStatus.NOT_FOUND);
        }
    }
    //TODO add validation for execute task
    public String executeTask(String taskId) {
        ProjectGenerationTask projectGenerationTask = projectGenerationUtil.get(taskId);
        if (!projectGenerationTask.getTaskStatus().name().equalsIgnoreCase(TaskStatus.CREATED.name())) {
            throw new TaskExecutionException("execution is not allowed", HttpStatus.METHOD_NOT_ALLOWED);
        }
        if (projectGenerationTask.getTaskType().name().equalsIgnoreCase(TaskType.GENERATE_FILE.name())) {
            return executeTaskForGenerateFile(taskId);
        } else {
            return executeTaskForCounter(projectGenerationTask);
        }
    }

    @Async
    String executeTaskForCounter(ProjectGenerationTask projectGenerationTask) {
        final float ProgressStartValue = 0f;
        String taskId = projectGenerationTask.getId();
        Integer endTime = projectGenerationTask.getEndTime();
        Integer startTime = projectGenerationTask.getStartTime();
        log.info("execute task for counter with Id: {}", taskId);

        log.info("Execute method asynchronously - {}", Thread.currentThread().getId());
        projectGenerationTaskRepository.updateTaskInfo(taskId, Thread.currentThread().getId(), TaskStatus.PROGRESSING);

        boolean isInterrupted = false;
        int duration = endTime - startTime;
        for(int counter = 1; counter <= duration; counter++){
            try {
                log.info("Task with Id {} is in progress", taskId);
                Thread.sleep(1000);
                counterService.updateProgress(taskId, duration, counter);
            } catch (InterruptedException e) {
                if (Thread.currentThread().getState().equals(Thread.State.TIMED_WAITING) ||
                        Thread.currentThread().getState().equals(Thread.State.WAITING) ||
                        Thread.currentThread().getState().equals(Thread.State.RUNNABLE)) {
                    projectGenerationTaskRepository.updateTaskStatus(taskId, TaskStatus.CREATED);
                    projectGenerationTaskRepository.updateProgress(taskId, ProgressStartValue);
                    isInterrupted = true;
                    break;
                }
            }
        }

        // set status of task to done!
        if (!isInterrupted) {
            projectGenerationTaskRepository.updateTaskStatus(taskId, TaskStatus.DONE);
        }

        return isInterrupted ? "task isInterrupted by user" : "Counter task finished successfully";
    }

    private String executeTaskForGenerateFile(String taskId) {
        log.info("execute task for generate file with Id: {}", taskId);
        URL url = Thread.currentThread().getContextClassLoader().getResource("challenge.zip");

        if (url == null) {
            throw new TaskExecutionException("Zip file not found", HttpStatus.NOT_FOUND);
        }
        try {
            fileService.storeResult(taskId, url);
            // set status of task to done!
            projectGenerationTaskRepository.updateTaskStatus(taskId, TaskStatus.DONE);
        } catch (Exception e) {
            throw new TaskExecutionException(e.getMessage(), e);
        }
        return "File is generated";
    }


    public String getProgress(String taskId) {
        StringBuilder result = new StringBuilder();
        final DecimalFormat df = new DecimalFormat("0.00");
        Float progress = projectGenerationUtil.get(taskId).getProgressPercentage();
        if(progress.doubleValue() == 1){
            result = result.append("Task Completed");
        } else {
            result = result.append("Progress of task is : " + df.format(progress) + " percentage");
        }
        return result.toString();
    }

    public ProjectGenerationTask getTask(String taskId) {
       return projectGenerationUtil.get(taskId);
    }
}
