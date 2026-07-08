import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { LoginResponse } from '../../app.responses';
import { HttpHeaders } from '@angular/common/http';
import LocalService from '../local/local.service';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private router = inject(Router);

  private type = 'Bearer';

  public saveBearer(loginResponse: LoginResponse) {
    LocalService.saveData(loginResponse.type, loginResponse.token);
  }

  public getBearer(): string {
    return LocalService.getData(this.type);
  }

  public isLoggedIn(): boolean {
    return LocalService.getData(this.type) !== '';
  }

  public getHeaderWithBearer(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${this.getBearer()}`,
    });
  }

  public getBearerOnlyHeader(): HttpHeaders {
    return new HttpHeaders({
      Authorization: `Bearer ${this.getBearer()}`,
    });
  }

  public logout(): void {
    LocalService.clearData();
    this.router.navigate(['/login']);
  }
}
