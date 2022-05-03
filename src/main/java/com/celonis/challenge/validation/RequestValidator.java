package com.celonis.challenge.validation;

import com.celonis.challenge.exceptions.TaskExecutionException;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.TaskStatus;
import com.celonis.challenge.model.TaskType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class RequestValidator {

    public void validateTaskCreationRequest(ProjectGenerationTask projectGenerationTask) {
        if (projectGenerationTask.getTaskType().name().equalsIgnoreCase(TaskType.GENERATE_FILE.name())) {
            if(projectGenerationTask.getStartTime() != null || projectGenerationTask.getEndTime() != null){
                throw new TaskExecutionException("start time and end time should be null for task type generate file"
                        , HttpStatus.BAD_REQUEST);
            }
        }else{
            if(projectGenerationTask.getStartTime() == null || projectGenerationTask.getEndTime() == null){
                throw new TaskExecutionException("start time and end time should be set for task type counter", HttpStatus.BAD_REQUEST);
            }else if(projectGenerationTask.getStartTime() > projectGenerationTask.getEndTime()){
                throw new TaskExecutionException("start time should be less than end time", HttpStatus.BAD_REQUEST);
            }
        }
    }

    public void checkTaskStatus(TaskStatus taskStatus) {
        if(!taskStatus.name().equalsIgnoreCase(TaskStatus.CREATED.name())){
            throw new TaskExecutionException("Only allowed to update the task with status created"
                    , HttpStatus.METHOD_NOT_ALLOWED);
        }
    }
}
