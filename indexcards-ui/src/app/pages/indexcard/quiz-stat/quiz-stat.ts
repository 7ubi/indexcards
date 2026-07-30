import {
  ChangeDetectorRef,
  Component,
  OnInit,
  inject,
  ChangeDetectionStrategy,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import HttpService from '../../../service/http/http.service';
import { Assessment, ProjectResponse } from '../../../app.responses';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { TranslatePipe } from '@ngx-translate/core';
import { LoadingSpinner } from '../../../component/loading-spinner/loading-spinner';
import {
  AssessmentChart,
  AssessmentSlice,
} from '../../../component/assessment-chart/assessment-chart';

const ASSESSMENT_COLORS: Record<Assessment, string> = {
  [Assessment.UNRATED]: '#9e9e9e',
  [Assessment.BAD]: '#c62828',
  [Assessment.OK]: '#f9a825',
  [Assessment.GOOD]: '#2e7d32',
};

@Component({
  selector: 'app-quiz-stat',
  imports: [MatButtonModule, MatCardModule, TranslatePipe, LoadingSpinner, AssessmentChart],
  templateUrl: './quiz-stat.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './quiz-stat.css',
})
export class QuizStat implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private httpService = inject(HttpService);
  private cdr = inject(ChangeDetectorRef);

  userProject?: ProjectResponse;

  id: string | null = '';

  slices: AssessmentSlice[] = [];

  loading = true;

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');

    this.httpService.get<ProjectResponse>(
      `/api/project/${this.id}`,
      (response) => {
        this.userProject = response;
        this.generateChartData();
        this.loading = false;
        this.cdr.detectChanges();
      },
      () => this.router.navigate(['']),
    );
  }

  private countForAssessment(assessment: Assessment): number {
    const assessmentName = Assessment[assessment];

    return (
      this.userProject?.indexCardResponses.filter(
        (indexCard) => (indexCard.assessment as unknown as string) === assessmentName,
      ).length ?? 0
    );
  }

  private generateChartData(): void {
    this.slices = [Assessment.UNRATED, Assessment.BAD, Assessment.OK, Assessment.GOOD].map(
      (assessment) => ({
        label: Assessment[assessment].toLowerCase(),
        color: ASSESSMENT_COLORS[assessment],
        count: this.countForAssessment(assessment),
      }),
    );
  }

  onClickQuizButton(): void {
    this.router.navigate(['/project', this.id, 'quiz']).then();
  }

  onClickToProject(): void {
    this.router.navigate(['/project', this.id]).then();
  }
}
