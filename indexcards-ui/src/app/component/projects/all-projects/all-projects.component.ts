import { Component, OnInit } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import {ResultResponse, UserProjectsResponse} from "../../../app.response";
import { LoginService } from "../../auth/login/login.service";
import { environment } from "../../../../environment/environment";
import { NotificationsService } from "angular2-notifications";
import {Router} from "@angular/router";

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
    private notificationService: NotificationsService,
    private router: Router
) {
  }

  ngOnInit(): void {
    this.getAllProjects();
  }

  getAllProjects() {
    this.http.get<UserProjectsResponse>(environment.apiUrl + 'project/projects'
      , { headers: this.loginService.getHeaderWithBearer() })
      .subscribe(
        response => {
          if(response.success) {
            this.userProjectsResponse = response;
          }
        }, err => {
          const errorMessages: ResultResponse = err.error;

          errorMessages.errorMessages.forEach(error => {
            this.notificationService.error(
              'ERROR',
              error.message
            );
          });
        }
      )
  }

  goToProject(id: number) {
    this.router.navigate(['/project', id]);
  }
}
