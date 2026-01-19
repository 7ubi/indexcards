import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {ProjectResponse} from "../../../app.response";
import {MessageService} from "primeng/api";
import {HttpService} from "../../../services/http.service";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.css']
})
export class ProjectComponent implements OnInit {

  userProject?: ProjectResponse;
  id: string | null = '';

  constructor(
    private route: ActivatedRoute,
    private httpService: HttpService,
    private messageService: MessageService,
    private router: Router,
    private translateService: TranslateService
  ) {
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');
    this.getIndexCards(this.id);
  }

  private getIndexCards(id: string | null) {
    this.httpService.get<ProjectResponse>('/api/project/project?id=' + id,
      (response) => this.userProject = response,
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

  deleteIndexCard(indexCardId: number) {
    this.httpService.delete<undefined>('/api/indexCard/delete', (_) => {
      this.messageService.add({
        key: 'tr',
        severity: 'success',
        summary: this.translateService.instant('common.success'),
        detail: this.translateService.instant('indexcard.deleted')
      });
      this.getIndexCards(this.id);

    }, () => this.router.navigate(['']), this.getDeleteRequest(indexCardId));
  }

  getDeleteRequest(indexCardId: number) {
    return {
      indexcardId: indexCardId
    };
  }

  editIndexCard(indexCardId: number) {
    this.router.navigate(['editIndexCard', indexCardId], {relativeTo: this.route}).then();
  }

  async onClickImportCsv(event: Event) {

    const file: File = (event.target as HTMLInputElement)!.files![0];
    if (file) {
      const text = await file.text();

      this.httpService.post<undefined>('/api/indexCard/import', {csv:text, projectId: this.id}, () => {
        this.messageService.add({
          key: 'tr',
          severity: 'success',
          summary: this.translateService.instant('common.success'),
          detail: this.translateService.instant('indexcard.imported')
        });
        this.getIndexCards(this.id);
      }
      , () => this.router.navigate(['']));
    }
  }
}

