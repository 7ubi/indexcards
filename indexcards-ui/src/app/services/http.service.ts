import {Injectable} from "@angular/core";
import {LoginService} from "../component/auth/login/login.service";
import {HttpClient} from "@angular/common/http";
import {ResultResponse} from "../app.response";
import {MessageService} from "primeng/api";
import {TranslateService} from "@ngx-translate/core";

@Injectable({
  providedIn: 'root'
})
export class HttpService {

  constructor(
    private http: HttpClient,
    private messageService: MessageService,
    private loginService: LoginService,
    private translateService: TranslateService
  ) {}

  post<Type>(url: string, request: any, subscribe: (response: Type) => void): void {
    this.http.post<Type>(url, request, { headers: this.loginService.getHeaderWithBearer()})
      .subscribe(
        response => subscribe(response)
        , err => {
          const response: ResultResponse = err.error;

          response.errorMessages.forEach(error => {
            this.messageService.add({
              key: 'tr',
              severity: 'error',
              summary: this.translateService.instant('common.error'),
              detail: error.message,
            });
          })
        }
      );
  }
}
