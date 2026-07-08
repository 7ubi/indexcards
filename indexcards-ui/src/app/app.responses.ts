export interface LoginResponse {
  readonly token: string;
  readonly type: string;
  readonly id: string;
  readonly username: string;
}

export interface IndexCardResponse {
  readonly indexCardId: number;
  readonly question: string;
  readonly answer: string;
  readonly assessment: Assessment;
  readonly dueDate: string;
}

export interface ProjectResponse {
  readonly id: number;
  readonly name: string;
  readonly examDate: string | null;
  readonly indexCardResponses: IndexCardResponse[];
}

export enum Assessment {
  UNRATED,
  BAD,
  OK,
  GOOD,
}
