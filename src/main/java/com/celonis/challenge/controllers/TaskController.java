package com.celonis.challenge.controllers;

import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.services.*;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Api("A REST-controller to handle various request to service")
@Slf4j
public class TaskController {

    private final TaskService taskService;

    private final FileService fileService;

    private final CounterService counterService;

    public TaskController(TaskService taskService,
                          FileService fileService,
                          CounterService counterService
    ) {
        this.taskService = taskService;
        this.fileService = fileService;
        this.counterService = counterService;
    }

    @GetMapping("/")
    public List<ProjectGenerationTask> listTasks() {
        return taskService.listTasks();
    }

    @PostMapping("/")
    public ProjectGenerationTask createTask(@RequestBody @Valid ProjectGenerationTask projectGenerationTask) {

        return taskService.createTask(projectGenerationTask);
    }

    @GetMapping("/{taskId}")
    public ProjectGenerationTask getTask(@PathVariable String taskId) {
        return taskService.getTask(taskId);
    }

    @PutMapping("/{taskId}")
    public ProjectGenerationTask updateTask(@PathVariable String taskId,
                                            @RequestBody @Valid ProjectGenerationTask projectGenerationTask) {
        return taskService.updateTask(taskId, projectGenerationTask);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable String taskId) {
        taskService.deleteTask(taskId);
    }

    @PostMapping("/{taskId}/execute")
    @ResponseStatus(HttpStatus.CREATED)
    public String executeTask(@PathVariable String taskId) {
        return taskService.executeTask(taskId);
    }

    @PostMapping("/{taskId}/interrupt")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void interrupt(@PathVariable String taskId) {
        counterService.interrupt(taskId);
    }

    @GetMapping("/{taskId}/fileGenerationResultTask")
    public ResponseEntity<FileSystemResource> getFileGenerationResultTask(@PathVariable String taskId) {
        return fileService.getFileGenerationResult(taskId);
    }

    @GetMapping("/{taskId}/counterResultTask")
    @ResponseStatus(HttpStatus.OK)
    public String getCounterResultTask(@PathVariable String taskId) {
        return counterService.getCounterResultTask(taskId);
    }

    @GetMapping("/{taskId}/progress")
    @ResponseStatus(HttpStatus.OK)
    public String getProgress(@PathVariable String taskId) {
        return taskService.getProgress(taskId);
    }

}
