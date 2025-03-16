import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BoardService } from '../../../services/board.service';
import { Board } from '../../models/models';

@Component({
  selector: 'app-board-list',
  templateUrl: './board-list.component.html',
})
export class BoardListComponent implements OnInit {
  boards: Board[] = [];
  isLoading = false;
  createBoardForm: FormGroup;
  editBoardForm: FormGroup;
  showCreateForm = false;
  editingBoardId: number | null = null;
  errorMessage = '';

  constructor(private boardService: BoardService, private fb: FormBuilder) {
    this.createBoardForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
    });

    this.editBoardForm = this.fb.group({
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

  startEditing(board: Board): void {
    this.editingBoardId = board.id!;
    this.editBoardForm.patchValue({
      name: board.name,
    });
  }

  cancelEditing(): void {
    this.editingBoardId = null;
    this.editBoardForm.reset();
  }

  updateBoard(boardId: number): void {
    if (this.editBoardForm.invalid) {
      return;
    }

    const boardName = this.editBoardForm.value.name;
    this.isLoading = true;

    this.boardService.updateBoard(boardId, { name: boardName }).subscribe({
      next: () => {
        this.isLoading = false;
        this.loadBoards();
        this.cancelEditing();
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage =
          error.error?.message || 'Failed to update board. Please try again.';
      },
    });
  }

  deleteBoard(boardId: number): void {
    if (
      !confirm(
        'Are you sure you want to delete this board? This action cannot be undone.'
      )
    ) {
      return;
    }

    this.isLoading = true;

    this.boardService.deleteBoard(boardId).subscribe({
      next: () => {
        this.isLoading = false;
        this.loadBoards();
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage =
          error.error?.message || 'Failed to delete board. Please try again.';
      },
    });
  }
}
