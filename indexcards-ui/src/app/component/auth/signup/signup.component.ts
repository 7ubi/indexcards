import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import {NgForm} from '@angular/forms';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {

  constructor(private http: HttpClient) {

  }
 
  createAccount(signup: NgForm) {
    this.http.post('http://localhost:8080/api/auth/signup', signup.value)
      .subscribe((res) => {
        console.log(res);
      });
  }
}
