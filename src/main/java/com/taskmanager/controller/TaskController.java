package com.taskmanager.controller;

import com.taskmanager.model.Task;
import com.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // GET /api/tasks?userId=1
    // Returns all tasks for a user
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(@RequestParam Long userId) {
        List<Task> tasks = taskService.getAllTasksForUser(userId);
        return ResponseEntity.ok(tasks);
    }

    // GET /api/tasks/pending?userId=1
    @GetMapping("/pending")
    public ResponseEntity<List<Task>> getPendingTasks(@RequestParam Long userId) {
        return ResponseEntity.ok(taskService.getPendingTasks(userId));
    }

    // GET /api/tasks/completed?userId=1
    @GetMapping("/completed")
    public ResponseEntity<List<Task>> getCompletedTasks(@RequestParam Long userId) {
        return ResponseEntity.ok(taskService.getCompletedTasks(userId));
    }

    // GET /api/tasks/{id}?userId=1
    @GetMapping("/{id}")
    public ResponseEntity<?> getTask(@PathVariable Long id, @RequestParam Long userId) {
        try {
            Task task = taskService.getTaskById(id, userId);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // POST /api/tasks
    // Body: { "userId": 1, "title": "Buy groceries", "description": "...", "priority": "HIGH", "dueDate": "2024-12-31" }
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Map<String, String> body) {
        try {
            Long userId     = Long.parseLong(body.get("userId"));
            String title    = body.get("title");
            String desc     = body.get("description");
            String priority = body.getOrDefault("priority", "MEDIUM");
            String dueDateStr = body.get("dueDate");

            if (title == null || title.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Title is required"));
            }

            LocalDate dueDate = (dueDateStr != null && !dueDateStr.isBlank())
                    ? LocalDate.parse(dueDateStr)
                    : null;

            Task task = taskService.createTask(userId, title, desc, priority, dueDate);
            return ResponseEntity.status(HttpStatus.CREATED).body(task);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // PUT /api/tasks/{id}
    // Update title, description, priority, dueDate
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            Long userId     = Long.parseLong(body.get("userId"));
            String title    = body.get("title");
            String desc     = body.get("description");
            String priority = body.getOrDefault("priority", "MEDIUM");
            String dueDateStr = body.get("dueDate");

            LocalDate dueDate = (dueDateStr != null && !dueDateStr.isBlank())
                    ? LocalDate.parse(dueDateStr)
                    : null;

            Task updated = taskService.updateTask(id, userId, title, desc, priority, dueDate);
            return ResponseEntity.ok(updated);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // PATCH /api/tasks/{id}/toggle?userId=1
    // Toggle completed status
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggleComplete(@PathVariable Long id, @RequestParam Long userId) {
        try {
            Task task = taskService.toggleComplete(id, userId);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE /api/tasks/{id}?userId=1
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, @RequestParam Long userId) {
        try {
            taskService.deleteTask(id, userId);
            return ResponseEntity.ok(Map.of("message", "Task deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
