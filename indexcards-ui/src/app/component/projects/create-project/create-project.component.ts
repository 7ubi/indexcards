import { Component } from '@angular/core';
import {NgForm} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {MessageResponse, ResultResponse} from "../../../app.response";
import {environment} from "../../../../environment/environment";
import {NotificationsService} from "angular2-notifications";
import {LoginService} from "../../auth/login/login.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-create-project',
  templateUrl: './create-project.component.html',
  styleUrls: ['./create-project.component.css']
})
export class CreateProjectComponent {

  constructor(
    private http: HttpClient,
    private notificationService: NotificationsService,
    private loginService: LoginService,
    private router: Router
  ){}

  createProject(createProject: NgForm): void {
    this.http.post<ResultResponse>(environment.apiUrl + 'project/create', createProject.value,
      { headers: this.loginService.getHeaderWithBearer() })
      .subscribe(
        response => {
          if(response.success) {
            this.notificationService.success("SUCCESS", "Project was created!");
            this.router.navigate(['/']);
          }
          response.errorMessages.forEach((error) => {
            this.notificationService.error(
              'ERROR',
              error.message
            );
          });
        }
      );
  }
}
