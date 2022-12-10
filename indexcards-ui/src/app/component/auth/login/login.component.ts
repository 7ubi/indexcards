import {Component, OnInit} from '@angular/core';
import { NgForm } from '@angular/forms';
import { HttpClient } from "@angular/common/http";
import {LoginResponse} from "../../../app.response";
import {LoginService} from "./login.service";
import {Router} from "@angular/router";
import {environment} from "../../../../environment/environment";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  constructor(private http: HttpClient, private router: Router, private loginService: LoginService) {
  }

  error: string = '';

  ngOnInit(): void {
    if(this.loginService.isLoggedIn()) {
      this.router.navigate(['/']);
    }
  }

  makeLogin(login: NgForm) {
    this.http.post<LoginResponse>(environment.apiUrl + 'auth/login', login.value)
      .subscribe(
        response => {
          this.loginService.saveBearer(response);

          this.router.navigate(['/']);
        }, error => {
          if(error.status === 401){
            this.error = 'Username not found or wrong password';
          }
        }
      );
  }
}
