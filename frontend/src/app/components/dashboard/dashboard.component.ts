import { Component, OnInit } from '@angular/core';
import { BoardService } from '../../services/board.service';
import { Board } from '../../components/models/models';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit {
  boards: Board[] = [];
  isLoading = false;
  createBoardForm: FormGroup;
  showCreateForm = false;
  errorMessage = '';

  constructor(private boardService: BoardService, private fb: FormBuilder) {
    this.createBoardForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
    });
  }

  ngOnInit(): void {
    this.loadBoards();
  }

  loadBoards(): void {
    this.isLoading = true;
    this.boardService.getBoards().subscribe({
      next: (boards) => {
        this.boards = boards;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = 'Failed to load boards. Please try again.';
      },
    });
  }

  toggleCreateForm(): void {
    this.showCreateForm = !this.showCreateForm;
    if (!this.showCreateForm) {
      this.createBoardForm.reset();
    }
  }

  createBoard(): void {
    if (this.createBoardForm.invalid) {
      return;
    }

    const boardName = this.createBoardForm.value.name;
    this.isLoading = true;

    this.boardService.createBoard({ name: boardName }).subscribe({
      next: () => {
        this.isLoading = false;
        this.loadBoards();
        this.toggleCreateForm();
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage =
          error.error?.message || 'Failed to create board. Please try again.';
      },
    });
  }
}
