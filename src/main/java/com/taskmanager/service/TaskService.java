package com.taskmanager.service;

import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import com.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    // Get all tasks for a user
    public List<Task> getAllTasksForUser(Long userId) {
        return taskRepository.findByUserId(userId);
    }

    // Get one task by ID (and verify it belongs to this user)
    public Task getTaskById(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        // Security check: make sure the task belongs to this user
        if (!task.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: this task does not belong to you");
        }
        return task;
    }

    // Create a new task
    public Task createTask(Long userId, String title, String description,
                           String priority, LocalDate dueDate) {
        User user = userService.findById(userId);
        Task task = new Task(title, description, priority, dueDate, user);
        return taskRepository.save(task);
    }

    // Update an existing task
    public Task updateTask(Long taskId, Long userId, String title,
                           String description, String priority, LocalDate dueDate) {
        Task task = getTaskById(taskId, userId);  // Also checks ownership
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setDueDate(dueDate);
        return taskRepository.save(task);
    }

    // Mark a task as complete or incomplete
    public Task toggleComplete(Long taskId, Long userId) {
        Task task = getTaskById(taskId, userId);
        task.setCompleted(!task.isCompleted());  // Flip the boolean
        return taskRepository.save(task);
    }

    // Delete a task
    public void deleteTask(Long taskId, Long userId) {
        Task task = getTaskById(taskId, userId);  // Also checks ownership
        taskRepository.delete(task);
    }

    // Get only pending tasks
    public List<Task> getPendingTasks(Long userId) {
        return taskRepository.findByUserIdAndCompleted(userId, false);
    }

    // Get only completed tasks
    public List<Task> getCompletedTasks(Long userId) {
        return taskRepository.findByUserIdAndCompleted(userId, true);
    }
}
