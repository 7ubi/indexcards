import { Component, OnInit } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { UserProjectsResponse } from "../../../app.response";
import { LoginService } from "../../auth/login/login.service";
import { environment } from "../../../../environment/environment";
import { NotificationsService } from "angular2-notifications";

@Component({
  selector: 'app-all-projects',
  templateUrl: './all-projects.component.html',
  styleUrls: ['./all-projects.component.css']
})
export class AllProjectsComponent implements OnInit {

  userProjectsResponse?: UserProjectsResponse;

  constructor(
    private http: HttpClient,
    private loginService: LoginService,
    private notificationService: NotificationsService
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
          response.errorMessages.forEach((error) => {
            this.notificationService.error(
              'ERROR',
              error.message
            );
          });
        }
      )
  }
}
