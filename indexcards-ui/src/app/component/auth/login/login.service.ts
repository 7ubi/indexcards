import {LocalService} from "../../../local.service";
import {Injectable} from "@angular/core";
import {LoginResponse} from "../../../app.response";

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  private static type: string = 'Bearer';

  constructor() {  }

  public static saveBearer(loginResponse: LoginResponse) {
    LocalService.saveEncryptedData(loginResponse.type, loginResponse.token);
  }

  public static getBearer(): string {
    return LocalService.getEncryptedData(this.type);
  }

  public static isLoggedIn(): boolean {
    return LocalService.getEncryptedData(this.type) !== '';
  }
}
