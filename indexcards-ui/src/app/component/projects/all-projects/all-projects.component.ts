import { Component, OnInit } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { ProjectResponse } from "../../../app.response";
import { LoginService } from "../../auth/login/login.service";
import {environment} from "../../../../environment/environment";

@Component({
  selector: 'app-all-projects',
  templateUrl: './all-projects.component.html',
  styleUrls: ['./all-projects.component.css']
})
export class AllProjectsComponent implements OnInit {

  projects: ProjectResponse[] | undefined;

  constructor(private http: HttpClient, private loginService: LoginService) {
  }

  ngOnInit(): void {
    this.getAllProjects();
  }

  getAllProjects() {
    this.http.get<ProjectResponse[]>(environment.apiUrl + 'project/projects'
      , { headers: this.loginService.getHeaderWithBearer() })
      .subscribe(
        response => {
          this.projects = response;
        }
      )
  }
}
