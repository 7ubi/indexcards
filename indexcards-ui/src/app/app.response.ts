export interface MessageResponse {
  readonly message: string;
}

export interface ResultResponse {
  readonly success: boolean;
  readonly errorMessages: MessageResponse[];
}

export interface LoginResponse {
  readonly token: string;
  readonly type: string;
  readonly id: string;
  readonly username: string;
}

export interface IndexCardResponse {
  readonly name: string;
}

export interface ProjectResponse {
  readonly name: string;
  readonly indexCardResponses: IndexCardResponse[];
}


