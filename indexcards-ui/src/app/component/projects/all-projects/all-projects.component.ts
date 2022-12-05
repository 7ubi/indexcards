import { Component, OnInit } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { ProjectResponse } from "../../../app.response";
import { LoginService } from "../../auth/login/login.service";

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
    this.http.get<ProjectResponse[]>('http://localhost:8080/api/project/projects'
      , { headers: this.loginService.getHeaderWithBearer() })
      .subscribe(
        response => {
          this.projects = response;
        }
      )
  }
}
