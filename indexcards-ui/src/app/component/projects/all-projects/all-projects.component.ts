import {Component, OnInit} from '@angular/core';
import {ProjectResponse} from "../../../app.response";
import {Router} from "@angular/router";
import {ConfirmationService, MessageService} from "primeng/api";
import {TranslateService} from "@ngx-translate/core";
import {HttpService} from "../../../services/http.service";

@Component({
  selector: 'app-all-projects',
  templateUrl: './all-projects.component.html',
  styleUrls: ['./all-projects.component.css']
})
export class AllProjectsComponent implements OnInit {

  userProjectsResponse?: ProjectResponse[];

  constructor(
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private router: Router,
    private translateService: TranslateService,
    private httpService: HttpService
  ) {
  }

  ngOnInit(): void {
    this.getAllProjects();
  }

  getAllProjects() {
    this.httpService.get<ProjectResponse[]>('/api/project/projects', response => {
      this.userProjectsResponse = response;
    });
  }

  goToProject(id: number) {
    this.router.navigate(['/project', id]).then();
  }

  deleteProject(id: number) {
    this.httpService.delete<undefined>(`/api/project/delete?id=${id}`, _ => {
      this.messageService.add({
        key: 'tr',
        severity: 'success',
        summary: this.translateService.instant('common.success'),
        detail: this.translateService.instant('project.deleted'),
      });

      this.getAllProjects();
    });
  }

  confirm(event: Event, id: number) {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: this.translateService.instant('project.delete_confirmation'),
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.deleteProject(id);
      },
      acceptLabel: this.translateService.instant('project.yes'),
      rejectLabel: this.translateService.instant('project.no')
    });
  }

  editProject(id: number) {
    this.router.navigate(['/project', id, 'edit']).then();
  }
}
