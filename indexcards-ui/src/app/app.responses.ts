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
}

export interface ProjectResponse {
  readonly id: number;
  readonly name: string;
  readonly indexCardResponses: IndexCardResponse[];
}

export enum Assessment {
  UNRATED,
  BAD,
  OK,
  GOOD
}
