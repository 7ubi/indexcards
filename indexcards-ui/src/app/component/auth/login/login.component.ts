import {Component, OnInit} from '@angular/core';
import { NgForm } from '@angular/forms';
import { HttpClient } from "@angular/common/http";
import {LoginResponse} from "../../../app.response";
import {LoginService} from "./login.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  constructor(private http: HttpClient, private router: Router) {
  }

  error: string = '';

  ngOnInit(): void {
    if(LoginService.isLoggedIn()) {
      this.router.navigate(['/']);
    }
  }

  makeLogin(login: NgForm) {
    this.http.post<LoginResponse>('http://localhost:8080/api/auth/login', login.value)
      .subscribe(
        response => {
          LoginService.saveBearer(response);

          this.router.navigate(['/']);
        }, error => {
          if(error.status === 401){
            this.error = 'Username not found or wrong password';
          }
        }
      );
  }
}
