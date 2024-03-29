import {LocalService} from "../../../services/local.service";
import {Injectable} from "@angular/core";
import {LoginResponse} from "../../../app.response";
import {HttpHeaders} from "@angular/common/http";
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  private type = 'Bearer';

  constructor(private router: Router) {
  }


  public saveBearer(loginResponse: LoginResponse) {
    LocalService.saveEncryptedData(loginResponse.type, loginResponse.token);
  }

  public getBearer(): string {
    return LocalService.getEncryptedData(this.type);
  }

  public isLoggedIn(): boolean {
    return LocalService.getEncryptedData(this.type) !== '';
  }

  public getHeaderWithBearer(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${this.getBearer()}`
    })
  }

  public logout(): void {
    LocalService.clearData();
    this.router.navigate(['/login']);
  }
}
