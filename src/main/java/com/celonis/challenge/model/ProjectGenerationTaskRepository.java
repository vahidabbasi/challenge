package com.celonis.challenge.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface ProjectGenerationTaskRepository extends JpaRepository<ProjectGenerationTask, String> {

    @Modifying
    @Query("update ProjectGenerationTask set threadId = :threadId, taskStatus = :taskStatus where id = :id")
    @Transactional
    int updateTaskInfo(@Param("id") String id, @Param("threadId") Long threadId,
                       @Param("taskStatus") TaskStatus taskStatus);

    @Modifying
    @Query("update ProjectGenerationTask set taskStatus = :taskStatus where id = :id")
    @Transactional
    int updateTaskStatus(@Param("id") String id, @Param("taskStatus") TaskStatus taskStatus);

    @Modifying
    @Query("delete from ProjectGenerationTask where creationDate < :creationDate")
    @Transactional
    int updateTaskStatus(@Param("creationDate") LocalDateTime creationDate);

    @Modifying
    @Query("update ProjectGenerationTask set progressPercentage = :progressPercentage where id = :id")
    @Transactional
    int updateProgress(@Param("id") String id, @Param("progressPercentage") Float progressPercentage);

}
