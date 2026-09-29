export interface LoginResponse {
  readonly token: string;
  readonly type: string;
  readonly id: string;
  readonly username: string;
}

export interface UserResponse {
  readonly username: string;
  readonly firstname: string;
  readonly surname: string;
  readonly admin: boolean;
}

export interface IndexCardResponse {
  readonly indexCardId: number;
  readonly question: string;
  readonly answer: string;
  readonly assessment: Assessment;
  readonly dueDate: string;
}

export interface DueIndexCardResponse extends IndexCardResponse {
  readonly projectId: number;
  readonly projectName: string;
}

export interface ImageUploadResponse {
  readonly imageId: string;
}

export interface ProjectResponse {
  readonly id: number;
  readonly name: string;
  readonly examDate: string | null;
  readonly archived: boolean;
  readonly indexCardResponses: IndexCardResponse[];
}

export interface AnalyticsTotalsResponse {
  readonly users: number;
  readonly activeProjects: number;
  readonly archivedProjects: number;
  readonly indexCards: number;
  readonly assessments: number;
}

export interface AssessmentDistributionResponse {
  readonly unrated: number;
  readonly bad: number;
  readonly ok: number;
  readonly good: number;
}

export interface DailyActivityResponse {
  readonly date: string;
  readonly assessments: number;
  readonly activeUsers: number;
}

export interface DailyCountResponse {
  readonly date: string;
  readonly count: number;
}

export interface UserAnalyticsResponse {
  readonly username: string;
  readonly createdAt: string | null;
  readonly projects: number;
  readonly indexCards: number;
  readonly assessments: number;
  readonly lastActivity: string | null;
  readonly admin: boolean;
}

export interface AnalyticsResponse {
  readonly totals: AnalyticsTotalsResponse;
  readonly assessmentDistribution: AssessmentDistributionResponse;
  readonly dailyActivity: DailyActivityResponse[];
  readonly dailySignups: DailyCountResponse[];
  readonly users: UserAnalyticsResponse[];
}

export enum Assessment {
  UNRATED,
  BAD,
  OK,
  GOOD,
}
