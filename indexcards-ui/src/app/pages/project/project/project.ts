import {ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {ProjectResponse} from '../../../app.responses';
import {ActivatedRoute, NavigationEnd, Router} from '@angular/router';
import HttpService from '../../../service/http/http.service';
import {SnackbarService} from '../../../service/snackbar/snackbar.service';
import {MatButtonModule} from '@angular/material/button';
import {CardOverview} from '../../../component/card-overview/card-overview';
import {TranslatePipe} from '@ngx-translate/core';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-project',
  imports: [MatButtonModule, CardOverview, TranslatePipe],
  templateUrl: './project.html',
  styleUrl: './project.css',
})
export class Project implements OnInit, OnDestroy {
  userProject?: ProjectResponse;
  id: string | null = '';
  showChild = false;

  private routerEventsSub?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private httpService: HttpService,
    private router: Router,
    private snackbarService: SnackbarService,
    private cdr: ChangeDetectorRef
  ) {
  }

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
    this.httpService.get<ProjectResponse>('/api/project/' + this.id,
      (response) => {
        this.userProject = response;
        this.cdr.detectChanges();
      },
      () => this.router.navigate(['']));
  }

  onClickCreateIndexcardButton() {
    this.router.navigate(['createIndexCard'], {relativeTo: this.route});
  }

  onClickQuizButton() {
    this.router.navigate(['quiz'], {relativeTo: this.route});
  }

  canStartQuiz() {
    return this.userProject?.indexCardResponses
      && this.userProject?.indexCardResponses?.length > 0
  }

  async onClickImportCsv(event: Event) {

    const file: File = (event.target as HTMLInputElement)!.files![0];
    if (file) {
      const text = await file.text();

      this.httpService.post<undefined>('/api/indexCard/import', {csv: text, projectId: this.id}, () => {
          this.snackbarService.showSuccessMessage('indexcard.imported');
          this.getIndexCards();
        }
        , () => this.router.navigate(['']));
    }
  }
}
