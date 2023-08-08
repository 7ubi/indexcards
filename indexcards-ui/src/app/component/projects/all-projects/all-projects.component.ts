import { Component, OnInit } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import {ResultResponse, UserProjectsResponse} from "../../../app.response";
import { LoginService } from "../../auth/login/login.service";
import {Router} from "@angular/router";
import {ConfirmationService, MessageService} from "primeng/api";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-all-projects',
  templateUrl: './all-projects.component.html',
  styleUrls: ['./all-projects.component.css']
})
export class AllProjectsComponent implements OnInit {

  userProjectsResponse!: UserProjectsResponse;

  constructor(
    private http: HttpClient,
    private loginService: LoginService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private router: Router,
    private translateService: TranslateService
  ) {}

  ngOnInit(): void {
    this.getAllProjects();
  }

  getAllProjects() {
    this.http.get<UserProjectsResponse>('/api/project/projects'
      , { headers: this.loginService.getHeaderWithBearer() })
      .subscribe(
        response => {
          if(response.success) {
            this.userProjectsResponse = response;
          }
          response.errorMessages.forEach((error) => {
            this.messageService.add({
              key: 'tr',
              severity: 'error',
              summary: this.translateService.instant('common.error'),
              detail: error.message,
            });
          });
        }
      )
  }

  goToProject(id: number) {
    this.router.navigate(['/project', id]);
  }

  deleteProject(id: number) {
    this.http.delete<ResultResponse>(`/api/project/delete?id=${id}`
      , { headers: this.loginService.getHeaderWithBearer() })
      .subscribe(response => {
        if(response.success) {
          this.messageService.add({
            key: 'tr',
            severity: 'success',
            summary: this.translateService.instant('common.success'),
            detail: this.translateService.instant('project.deleted'),
          });

          const index
            = this.userProjectsResponse.projectResponses.findIndex(project => project.id === id);
          this.userProjectsResponse.projectResponses.splice(index);
        }

        response.errorMessages.forEach((error) => {
          this.messageService.add({
            key: 'tr',
            severity: 'error',
            summary: this.translateService.instant('common.error'),
            detail: error.message,
          });
        });
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
    this.router.navigate(['/project', id, 'edit']);
  }
}
