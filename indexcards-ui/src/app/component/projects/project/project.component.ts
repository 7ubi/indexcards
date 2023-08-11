import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {UserProjectResponse} from "../../../app.response";
import {LoginService} from "../../auth/login/login.service";
import {MessageService} from "primeng/api";
import {HttpService} from "../../../services/http.service";

@Component({
  selector: 'app-project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.css']
})
export class ProjectComponent implements OnInit{

  userProject?: UserProjectResponse;

  constructor(
    private route: ActivatedRoute,
    private httpService: HttpService,
    private messageService: MessageService,
    private loginService: LoginService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    this.httpService.get<UserProjectResponse>('/api/project/project?id=' + id,
      (response) => this.userProject = response,
        () => this.router.navigate(['']));
  }

  onClickCreateIndexcardButton() {
    this.router.navigate(['createIndexCard'], {relativeTo: this.route});
  }

  onClickQuizButton() {
    this.router.navigate(['quiz'], {relativeTo: this.route});
  }
}
