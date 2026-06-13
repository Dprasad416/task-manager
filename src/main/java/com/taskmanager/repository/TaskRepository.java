package com.taskmanager.repository;

import com.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Get all tasks for a specific user
    List<Task> findByUserId(Long userId);

    // Get only completed or pending tasks for a user
    List<Task> findByUserIdAndCompleted(Long userId, boolean completed);

    // Get tasks by priority for a user
    List<Task> findByUserIdAndPriority(Long userId, String priority);

    // Count how many tasks a user has
    long countByUserId(Long userId);
}
