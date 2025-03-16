import { Component, Input } from '@angular/core';
import { BoardService } from '../../../services/board.service';
import { Board } from '../../models/models';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
})
export class SidebarComponent {
  @Input() boardId?: number;
  boards: Board[] = [];
  isLoading = false;

  constructor(private boardService: BoardService) {}

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
      },
    });
  }
}
