package com.celonis.challenge.services;

import com.celonis.challenge.exceptions.TaskExecutionException;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.ProjectGenerationTaskRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;

@Service
public class FileService {

    private final TaskService taskService;

    private final ProjectGenerationTaskRepository projectGenerationTaskRepository;

    public FileService(@Lazy TaskService taskService,
                       ProjectGenerationTaskRepository projectGenerationTaskRepository) {
        this.taskService = taskService;
        this.projectGenerationTaskRepository = projectGenerationTaskRepository;
    }

    public ResponseEntity<FileSystemResource> getFileGenerationResult(String taskId) {
        ProjectGenerationTask projectGenerationTask = taskService.getTask(taskId);
        File inputFile = new File(projectGenerationTask.getStorageLocation());

        if (!inputFile.exists()) {
            throw new TaskExecutionException("File not generated yet", HttpStatus.NOT_FOUND);
        }

        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        respHeaders.setContentDispositionFormData("attachment", "challenge.zip");

        return new ResponseEntity<>(new FileSystemResource(inputFile), respHeaders, HttpStatus.OK);
    }

    public void storeResult(String taskId, URL url) throws IOException {
        ProjectGenerationTask projectGenerationTask = taskService.getTask(taskId);
        File outputFile = File.createTempFile(taskId, ".zip");
        outputFile.deleteOnExit();
        projectGenerationTask.setStorageLocation(outputFile.getAbsolutePath());
        projectGenerationTaskRepository.save(projectGenerationTask);
        try (InputStream is = url.openStream(); OutputStream os = new FileOutputStream(outputFile)) {
            IOUtils.copy(is, os);
        }
    }

}
