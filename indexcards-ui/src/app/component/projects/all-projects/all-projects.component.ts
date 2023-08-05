import { Component, OnInit } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import {ResultResponse, UserProjectsResponse} from "../../../app.response";
import { LoginService } from "../../auth/login/login.service";
import {Router} from "@angular/router";
import {MessageService} from "primeng/api";

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
    private router: Router
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
              summary: 'ERROR',
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
            summary: 'SUCCESS',
            detail: 'Project was deleted successfully!',
          });

          const index
            = this.userProjectsResponse.projectResponses.findIndex(project => project.id === id);
          this.userProjectsResponse.projectResponses.splice(index);
        }

        response.errorMessages.forEach((error) => {
          this.messageService.add({
            key: 'tr',
            severity: 'error',
            summary: 'ERROR',
            detail: error.message,
          });
        });
    });
  }
}
