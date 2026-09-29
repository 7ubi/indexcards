import {
  ChangeDetectorRef,
  Component,
  OnInit,
  ViewChild,
  inject,
  ChangeDetectionStrategy,
} from '@angular/core';
import { DatePipe } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { MatCard, MatCardContent, MatCardHeader, MatCardTitle } from '@angular/material/card';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable,
  MatTableDataSource,
} from '@angular/material/table';
import HttpService from '../../../service/http/http.service';
import { SnackbarService } from '../../../service/snackbar/snackbar.service';
import { ConfirmDialog, ConfirmDialogData } from '../../../component/confirm-dialog/confirm-dialog';
import {
  AnalyticsResponse,
  DailyCountResponse,
  UserAnalyticsResponse,
} from '../../../app.responses';
import { LoadingSpinner } from '../../../component/loading-spinner/loading-spinner';
import {
  AssessmentChart,
  AssessmentSlice,
} from '../../../component/assessment-chart/assessment-chart';
import { Bar, BarChart } from '../../../component/bar-chart/bar-chart';

const ASSESSMENT_COLORS = {
  unrated: '#9e9e9e',
  bad: '#c62828',
  ok: '#f9a825',
  good: '#2e7d32',
};

@Component({
  selector: 'app-analytics',
  imports: [
    DatePipe,
    TranslatePipe,
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatCardTitle,
    MatTable,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatCell,
    MatCellDef,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    MatSort,
    MatSortHeader,
    MatButton,
    MatIcon,
    LoadingSpinner,
    AssessmentChart,
    BarChart,
  ],
  templateUrl: './analytics.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './analytics.css',
})
export class Analytics implements OnInit {
  private httpService = inject(HttpService);
  private snackbarService = inject(SnackbarService);
  private dialog = inject(MatDialog);
  private cdr = inject(ChangeDetectorRef);

  analytics?: AnalyticsResponse;
  loading = true;

  slices: AssessmentSlice[] = [];
  assessmentBars: Bar[] = [];
  activeUserBars: Bar[] = [];
  signupBars: Bar[] = [];

  users = new MatTableDataSource<UserAnalyticsResponse>([]);
  columns = [
    'username',
    'createdAt',
    'projects',
    'indexCards',
    'assessments',
    'lastActivity',
    'actions',
  ];

  // The table only exists once loaded, so the sort is attached when it appears.
  @ViewChild(MatSort)
  set sort(sort: MatSort | undefined) {
    if (sort) {
      this.users.sort = sort;
    }
  }

  ngOnInit(): void {
    this.loadAnalytics();
  }

  loadAnalytics(): void {
    this.httpService.get<AnalyticsResponse>('/api/admin/analytics', (response) => {
      this.analytics = response;
      this.slices = (['unrated', 'bad', 'ok', 'good'] as const).map((assessment) => ({
        label: assessment,
        color: ASSESSMENT_COLORS[assessment],
        count: response.assessmentDistribution[assessment],
      }));
      this.assessmentBars = response.dailyActivity.map((day) => toBar(day.date, day.assessments));
      this.activeUserBars = response.dailyActivity.map((day) => toBar(day.date, day.activeUsers));
      this.signupBars = response.dailySignups.map((day: DailyCountResponse) =>
        toBar(day.date, day.count),
      );
      this.users.data = response.users;
      this.loading = false;
      this.cdr.detectChanges();
    });
  }

  makeAdmin(username: string): void {
    const dialogRef = this.dialog.open<ConfirmDialog, ConfirmDialogData, boolean>(ConfirmDialog, {
      width: '480px',
      maxWidth: '90vw',
      autoFocus: 'dialog',
      data: { username, action: 'make_admin', keyPrefix: 'admin.make_admin' },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result !== true) {
        return;
      }
      this.httpService.put(`/api/admin/users/${encodeURIComponent(username)}/admin`, {}, () => {
        this.snackbarService.showSuccessMessage('admin.made_admin');
        this.loadAnalytics();
      });
    });
  }
}

/** ISO date (yyyy-MM-dd) to a short dd.MM. label. */
function toBar(date: string, value: number): Bar {
  const [, month, day] = date.split('-');
  return { label: `${day}.${month}.`, value };
}
