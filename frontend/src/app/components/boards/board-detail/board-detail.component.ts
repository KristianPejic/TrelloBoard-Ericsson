import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BoardService } from '../../../services/board.service';
import { TaskService } from '../../../services/task.service';
import { Board, Task } from '../../../models/models';

@Component({
  selector: 'app-board-detail',
  templateUrl: './board-detail.component.html',
})
export class BoardDetailComponent implements OnInit {
  boardId!: number;
  board: Board | null = null;
  tasks: Task[] = [];
  tasksByStatus: { [key: string]: Task[] } = {
    TO_DO: [],
    IN_PROGRESS: [],
    DONE: [],
  };

  isLoading = false;
  errorMessage = '';

  createTaskForm: FormGroup;
  showCreateForm = false;

  editTaskForm: FormGroup;
  editingTaskId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private boardService: BoardService,
    private taskService: TaskService,
    private fb: FormBuilder
  ) {
    this.createTaskForm = this.fb.group({
      title: ['', [Validators.required]],
      description: [''],
      priority: ['MEDIUM'],
    });

    this.editTaskForm = this.fb.group({
      title: ['', [Validators.required]],
      description: [''],
      priority: ['MEDIUM'],
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.boardId = +params['id'];
      this.loadBoard();
      this.loadTasks();
    });
  }

  loadBoard(): void {
    this.isLoading = true;
    this.boardService.getBoard(this.boardId).subscribe({
      next: (board) => {
        this.board = board;
        this.isLoading = false;
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage =
          error.error?.message || 'Failed to load board. Please try again.';
      },
    });
  }

  loadTasks(): void {
    this.isLoading = true;
    this.taskService.getTasksByBoard(this.boardId).subscribe({
      next: (tasks) => {
        this.tasks = tasks;
        this.groupTasksByStatus();
        this.isLoading = false;
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage =
          error.error?.message || 'Failed to load tasks. Please try again.';
      },
    });
  }

  groupTasksByStatus(): void {
    // Reset task groups
    this.tasksByStatus = {
      TO_DO: [],
      IN_PROGRESS: [],
      DONE: [],
    };

    // Group tasks by status
    this.tasks.forEach((task) => {
      if (this.tasksByStatus[task.status]) {
        this.tasksByStatus[task.status].push(task);
      }
    });
  }

  toggleCreateForm(): void {
    this.showCreateForm = !this.showCreateForm;
    if (!this.showCreateForm) {
      this.createTaskForm.reset({
        priority: 'MEDIUM',
      });
    }
  }

  createTask(): void {
    if (this.createTaskForm.invalid) {
      return;
    }

    const { title, description, priority } = this.createTaskForm.value;
    const newTask: Task = {
      title,
      description,
      boardId: this.boardId,
      status: 'TO_DO',
      priority,
      completed: false,
    };

    this.isLoading = true;
    this.taskService.createTask(newTask).subscribe({
      next: () => {
        this.isLoading = false;
        this.loadTasks();
        this.toggleCreateForm();
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage =
          error.error?.message || 'Failed to create task. Please try again.';
      },
    });
  }

  startEditingTask(task: Task): void {
    this.editingTaskId = task.id!;
    this.editTaskForm.patchValue({
      title: task.title,
      description: task.description || '',
      priority: task.priority || 'MEDIUM',
    });
  }

  cancelEditingTask(): void {
    this.editingTaskId = null;
    this.editTaskForm.reset({
      priority: 'MEDIUM',
    });
  }

  updateTask(task: Task): void {
    if (this.editTaskForm.invalid) {
      return;
    }

    const { title, description, priority } = this.editTaskForm.value;
    const updatedTask: Task = {
      ...task,
      title,
      description,
      priority,
    };

    this.isLoading = true;
    this.taskService.updateTask(task.id!, updatedTask).subscribe({
      next: () => {
        this.isLoading = false;
        this.loadTasks();
        this.cancelEditingTask();
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage =
          error.error?.message || 'Failed to update task. Please try again.';
      },
    });
  }

  updateTaskStatus(
    task: Task,
    newStatus: 'TO_DO' | 'IN_PROGRESS' | 'DONE'
  ): void {
    if (task.status === newStatus) {
      return;
    }

    this.isLoading = true;
    this.taskService.updateTaskStatus(task.id!, newStatus).subscribe({
      next: () => {
        this.isLoading = false;
        this.loadTasks();
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage =
          error.error?.message ||
          'Failed to update task status. Please try again.';
      },
    });
  }

  deleteTask(taskId: number): void {
    if (
      !confirm(
        'Are you sure you want to delete this task? This action cannot be undone.'
      )
    ) {
      return;
    }

    this.isLoading = true;
    this.taskService.deleteTask(taskId).subscribe({
      next: () => {
        this.isLoading = false;
        this.loadTasks();
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage =
          error.error?.message || 'Failed to delete task. Please try again.';
      },
    });
  }
}
