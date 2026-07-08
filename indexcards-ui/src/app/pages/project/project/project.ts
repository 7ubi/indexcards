import {
  ChangeDetectorRef,
  Component,
  OnDestroy,
  OnInit,
  inject,
  ChangeDetectionStrategy,
} from '@angular/core';
import { ProjectResponse } from '../../../app.responses';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import HttpService from '../../../service/http/http.service';
import { SnackbarService } from '../../../service/snackbar/snackbar.service';
import { MatButtonModule } from '@angular/material/button';
import { CardOverview } from '../../../component/card-overview/card-overview';
import { TranslatePipe } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { LoadingSpinner } from '../../../component/loading-spinner/loading-spinner';

function escapeCsvField(value: string): string {
  return `"${value.replace(/"/g, '""')}"`;
}

@Component({
  selector: 'app-project',
  imports: [MatButtonModule, CardOverview, TranslatePipe, MatIcon, MatTooltip, LoadingSpinner],
  templateUrl: './project.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './project.css',
})
export class Project implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private httpService = inject(HttpService);
  private router = inject(Router);
  private snackbarService = inject(SnackbarService);
  private cdr = inject(ChangeDetectorRef);

  userProject?: ProjectResponse;
  id: string | null = '';
  showChild = false;
  loading = true;

  private routerEventsSub?: Subscription;

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');
    this.getIndexCards();

    // Show child when a child route like 'quiz' is active
    this.routerEventsSub = this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        const child = this.route.firstChild;
        this.showChild = !!child && !!child.snapshot;
        this.cdr.detectChanges();
      }
    });
  }

  ngOnDestroy(): void {
    this.routerEventsSub?.unsubscribe();
  }

  getIndexCards() {
    this.loading = true;
    this.httpService.get<ProjectResponse>(
      '/api/project/' + this.id,
      (response) => {
        this.userProject = response;
        this.loading = false;
        this.cdr.detectChanges();
      },
      () => this.router.navigate(['']),
    );
  }

  onClickCreateIndexcardButton() {
    this.router.navigate(['createIndexCard'], { relativeTo: this.route });
  }

  onClickQuizButton() {
    this.router.navigate(['quiz'], { relativeTo: this.route });
  }

  onClickStatButton() {
    this.router.navigate(['quiz', 'stat'], { relativeTo: this.route });
  }

  hasIndexCards() {
    return !!this.userProject?.indexCardResponses && this.userProject.indexCardResponses.length > 0;
  }

  canStartQuiz() {
    return this.hasIndexCards();
  }

  async onClickImportCsv(event: Event) {
    const file: File = (event.target as HTMLInputElement)!.files![0];
    if (file) {
      const text = await file.text();

      this.loading = true;
      this.httpService.post<undefined>(
        '/api/indexCard/import',
        { csv: text, projectId: this.id },
        () => {
          this.snackbarService.showSuccessMessage('indexcard.imported');
          this.getIndexCards();
        },
        () => this.router.navigate(['']),
      );
    }
  }

  onClickExportCsv() {
    const cards = this.userProject?.indexCardResponses ?? [];
    const csv = cards
      .map((card) => `${escapeCsvField(card.question)},${escapeCsvField(card.answer)}`)
      .join('\r\n');

    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `${this.userProject?.name ?? 'indexcards'}.csv`;
    link.click();
    URL.revokeObjectURL(url);
  }
}
