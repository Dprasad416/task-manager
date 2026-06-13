package com.taskmanager;

import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.service.TaskService;
import com.taskmanager.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    private User testUser;
    private Task testTask;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User("john", "john@example.com", "hashed_pass");
        testUser.setId(1L);

        testTask = new Task("Buy groceries", "Milk, eggs, bread", "HIGH", LocalDate.now(), testUser);
        testTask.setId(1L);
    }

    @Test
    void getAllTasksForUser_returnsTaskList() {
        when(taskRepository.findByUserId(1L)).thenReturn(Arrays.asList(testTask));

        List<Task> result = taskService.getAllTasksForUser(1L);

        assertEquals(1, result.size());
        assertEquals("Buy groceries", result.get(0).getTitle());
    }

    @Test
    void createTask_savesAndReturnsTask() {
        when(userService.findById(1L)).thenReturn(testUser);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        Task result = taskService.createTask(1L, "Buy groceries", "Milk", "HIGH", LocalDate.now());

        assertNotNull(result);
        assertEquals("Buy groceries", result.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void toggleComplete_flipsCompletedStatus() {
        assertFalse(testTask.isCompleted());  // starts as false

        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        Task result = taskService.toggleComplete(1L, 1L);

        assertTrue(result.isCompleted());  // should now be true
    }

    @Test
    void deleteTask_callsRepositoryDelete() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        taskService.deleteTask(1L, 1L);

        verify(taskRepository, times(1)).delete(testTask);
    }

    @Test
    void getTaskById_throwsWhenTaskNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> taskService.getTaskById(99L, 1L));

        assertTrue(ex.getMessage().contains("Task not found"));
    }

    @Test
    void getTaskById_throwsWhenWrongUser() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> taskService.getTaskById(1L, 999L));  // Wrong userId

        assertTrue(ex.getMessage().contains("Access denied"));
    }
}
