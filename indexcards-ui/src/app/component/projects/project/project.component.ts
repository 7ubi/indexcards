import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {NotificationsService} from "angular2-notifications";
import {UserProjectResponse} from "../../../app.response";
import {environment} from "../../../../environment/environment";
import {LoginService} from "../../auth/login/login.service";

@Component({
  selector: 'app-project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.css']
})
export class ProjectComponent implements OnInit{

  userProject?: UserProjectResponse;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private notificationService: NotificationsService,
    private loginService: LoginService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    this.http.get<UserProjectResponse>(environment.apiUrl + 'project/project?id=' + id,
      { headers: this.loginService.getHeaderWithBearer()})
      .subscribe(
        response => {
          if(response.success) {
            this.userProject = response;
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

  onClickCreateIndexcardButton() {
    this.router.navigate(['createIndexCard'], {relativeTo: this.route});
  }
}
