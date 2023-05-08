import {LocalService} from "../../../services/local.service";
import {Injectable} from "@angular/core";
import {LoginResponse} from "../../../app.response";
import {HttpHeaders} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  private type: string = 'Bearer';

  constructor() {  }

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
}
