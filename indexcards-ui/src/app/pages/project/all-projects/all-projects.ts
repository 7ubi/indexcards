import {
  ChangeDetectionStrategy,
  Component,
  computed,
  model,
  ModelSignal,
  OnInit,
  signal,
  WritableSignal,
  inject,
} from '@angular/core';
import { DueIndexCardResponse, ProjectResponse } from '../../../app.responses';
import { Router, RouterLink } from '@angular/router';
import HttpService from '../../../service/http/http.service';
import { SnackbarService } from '../../../service/snackbar/snackbar.service';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { ConfirmDialog, ConfirmDialogData } from '../../../component/confirm-dialog/confirm-dialog';
import { DueDialog, DueDialogData, DueProject } from '../../../component/due-dialog/due-dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { MatIcon } from '@angular/material/icon';
import { LoadingSpinner } from '../../../component/loading-spinner/loading-spinner';
import { DatePipe, NgTemplateOutlet } from '@angular/common';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatExpansionModule } from '@angular/material/expansion';
import { ProjectArchiveService } from '../../../service/project/project-archive.service';

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
    MatExpansionModule,
    NgTemplateOutlet,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AllProjects implements OnInit {
  private snackbarService = inject(SnackbarService);
  private router = inject(Router);
  private httpService = inject(HttpService);
  private dialog = inject(MatDialog);
  private projectArchiveService = inject(ProjectArchiveService);

  readonly project: ModelSignal<ProjectResponse> = model({} as ProjectResponse);
  userProjectsResponse: WritableSignal<ProjectResponse[]> = signal([]);
  loading: WritableSignal<boolean> = signal(true);
  readonly activeProjects = computed(() =>
    this.userProjectsResponse().filter((project) => !project.archived),
  );
  readonly archivedProjects = computed(() =>
    this.userProjectsResponse().filter((project) => project.archived),
  );

  ngOnInit(): void {
    this.getAllProjects();
    this.getDueIndexCards();
  }

  getAllProjects() {
    this.httpService.get<ProjectResponse[]>('/api/project', (response: ProjectResponse[]) => {
      this.userProjectsResponse.set(response);
      this.loading.set(false);
    });
  }

  getDueIndexCards() {
    this.httpService.get<DueIndexCardResponse[]>(
      '/api/indexCard/due',
      (response: DueIndexCardResponse[]) => {
        if (response.length > 0) {
          this.openDueDialog(response);
        }
      },
    );
  }

  openDueDialog(dueIndexCards: DueIndexCardResponse[]): void {
    // Cards arrive most overdue first, so projects keep the order of their most overdue card.
    const projects = new Map<number, DueProject>();
    for (const indexCard of dueIndexCards) {
      const project = projects.get(indexCard.projectId);
      if (project) {
        project.cardCount++;
      } else {
        projects.set(indexCard.projectId, { projectName: indexCard.projectName, cardCount: 1 });
      }
    }

    const dialogRef = this.dialog.open<DueDialog, DueDialogData, boolean>(DueDialog, {
      width: '600px',
      maxWidth: '90vw',
      // Focus the dialog itself, otherwise the close button is focused and shows its focus background on open.
      autoFocus: 'dialog',
      data: {
        cardCount: dueIndexCards.length,
        projects: [...projects.values()],
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === true) {
        this.studyAllDue();
      }
    });
  }

  studyAllDue() {
    this.router.navigate(['/due']).then();
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

    const dialogRef = this.dialog.open<ConfirmDialog, ConfirmDialogData, boolean>(ConfirmDialog, {
      width: '480px',
      maxWidth: '90vw',
      autoFocus: 'dialog',
      data: { project: this.project(), action: 'delete' },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === true) {
        this.deleteProject(project.id);
        this.getAllProjects();
      }
    });
  }

  toggleArchive(project: ProjectResponse) {
    this.projectArchiveService.toggleArchive(project, () => this.getAllProjects());
  }

  editProject(id: number) {
    this.router.navigate(['/project', id, 'edit']).then();
  }
}
