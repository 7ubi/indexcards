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
  readonly question: string;
  readonly answer:string;
  readonly assessment: Assessment;
}

export interface ProjectResponse {
  readonly id: number;
  readonly name: string;
  readonly indexCardResponses: IndexCardResponse[];
}

export interface UserProjectsResponse extends ResultResponse {
  readonly projectResponses: ProjectResponse[];
}

export interface UserProjectResponse extends ResultResponse {
  readonly projectResponse: ProjectResponse;
}

export enum Assessment {
  UNRATED,
  BAD,
  OK,
  GOOD
}

