import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import {HttpClient} from "@angular/common/http";
import {LoginService} from "./login.service";
import {map} from "rxjs";
import {LocalService} from "../../../local.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  constructor(private http: HttpClient) {
  }

  makeLogin(login: NgForm) {
    this.http.post<LoginService>('http://localhost:8080/api/auth/login', login.value)
      .pipe(
        map(response => {
          return new LoginService(
            response.token,
            response.type,
            response.id,
            response.username
          );
        })
      ).subscribe(
        response => {
          LocalService.saveEncryptedData(response.type, response.token);
        }
    );
  }
}
