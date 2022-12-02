import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { HttpClient } from "@angular/common/http";
import {LoginResponse} from "../../../app.response";
import {LoginService} from "./login.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  constructor(private http: HttpClient) {
  }

  makeLogin(login: NgForm) {
    this.http.post<LoginResponse>('http://localhost:8080/api/auth/login', login.value)
      .subscribe(
        response => {
          LoginService.saveBearer(response);
        }
      );
  }
}
