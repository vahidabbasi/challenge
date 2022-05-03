package com.celonis.challenge.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectGenerationTask {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String name;

    @JsonIgnore
    private Long threadId;

    private Integer startTime;

    @JsonIgnore
    private Float progressPercentage;

    private Integer endTime;

    @Enumerated(EnumType.STRING)
    private TaskType taskType;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus = TaskStatus.CREATED;

    @JsonIgnore
    private LocalDateTime creationDate;

    @JsonIgnore
    private String storageLocation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public Float getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Float progressPercentage) {
        this.progressPercentage = progressPercentage;
    }
}
