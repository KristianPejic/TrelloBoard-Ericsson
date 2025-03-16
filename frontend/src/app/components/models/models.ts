export interface User {
  id?: number;
  username: string;
  email: string;
  password?: string;
  token?: string;
}

export interface Board {
  id?: number;
  name: string;
  userId?: number;
}

export interface Task {
  id?: number;
  title: string;
  description?: string;
  completed: boolean;
  boardId: number;
  status: 'TO_DO' | 'IN_PROGRESS' | 'DONE';
  priority?: 'LOW' | 'MEDIUM' | 'HIGH';
  createdAt?: Date;
  updatedAt?: Date;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface ErrorResponse {
  message: string;
  statusCode?: number;
}
