import {
  ChangeDetectionStrategy,
  Component,
  model,
  ModelSignal,
  OnInit,
  signal,
  WritableSignal,
  inject,
} from '@angular/core';
import { ProjectResponse } from '../../../app.responses';
import { Router, RouterLink } from '@angular/router';
import HttpService from '../../../service/http/http.service';
import { SnackbarService } from '../../../service/snackbar/snackbar.service';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { ConfirmDialog } from '../../../component/confirm-dialog/confirm-dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { MatIcon } from '@angular/material/icon';
import { LoadingSpinner } from '../../../component/loading-spinner/loading-spinner';
import { DatePipe } from '@angular/common';
import { MatTooltipModule } from '@angular/material/tooltip';

export interface DialogData {
  project: ProjectResponse;
}

@Component({
  selector: 'app-all-projects',
  templateUrl: './all-projects.html',
  styleUrl: './all-projects.css',
  imports: [
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    MatButtonModule,
    TranslatePipe,
    MatIcon,
    RouterLink,
    LoadingSpinner,
    DatePipe,
    MatTooltipModule,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AllProjects implements OnInit {
  private snackbarService = inject(SnackbarService);
  private router = inject(Router);
  private httpService = inject(HttpService);
  private dialog = inject(MatDialog);

  readonly project: ModelSignal<ProjectResponse> = model({} as ProjectResponse);
  userProjectsResponse: WritableSignal<ProjectResponse[]> = signal([]);
  loading: WritableSignal<boolean> = signal(true);

  ngOnInit(): void {
    this.getAllProjects();
  }

  getAllProjects() {
    this.httpService.get<ProjectResponse[]>('/api/project', (response: ProjectResponse[]) => {
      this.userProjectsResponse.set(response);
      this.loading.set(false);
    });
  }

  goToProject(id: number) {
    this.router.navigate(['/project', id]).then();
  }

  getInitial(name: string): string {
    return name?.trim().charAt(0).toUpperCase() || '?';
  }

  examDateStatus(examDate: string | null): 'overdue' | 'soon' | 'normal' | null {
    if (!examDate) {
      return null;
    }

    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const exam = new Date(examDate);
    exam.setHours(0, 0, 0, 0);

    const daysUntilExam = (exam.getTime() - today.getTime()) / (1000 * 60 * 60 * 24);

    if (daysUntilExam < 0) {
      return 'overdue';
    }
    if (daysUntilExam <= 7) {
      return 'soon';
    }
    return 'normal';
  }

  deleteProject(id: number) {
    this.httpService.delete<undefined>(`/api/project?id=${id}`, () => {
      this.snackbarService.showSuccessMessage('project.deleted');

      this.getAllProjects();
    });
  }

  openDialog(project: ProjectResponse): void {
    this.project.set(project);

    const dialogRef = this.dialog.open(ConfirmDialog, {
      data: { project: this.project() },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === true) {
        this.deleteProject(project.id);
        this.getAllProjects();
      }
    });
  }

  editProject(id: number) {
    this.router.navigate(['/project', id, 'edit']).then();
  }
}
