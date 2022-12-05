import {LocalService} from "../../../local.service";
import {Injectable} from "@angular/core";
import {LoginResponse} from "../../../app.response";

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
}
