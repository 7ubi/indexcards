import {Injectable} from "@angular/core";
import {LoginService} from "../component/auth/login/login.service";
import {HttpClient} from "@angular/common/http";
import {ResultResponse} from "../app.response";
import {MessageService} from "primeng/api";
import {TranslateService} from "@ngx-translate/core";
import {Observable} from "rxjs";

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

  private async error(error: ResultResponse) {
    error.errorMessages.forEach(error => {
      this.messageService.add({
        key: 'tr',
        severity: 'error',
        summary: this.translateService.instant('common.error'),
        detail: error.message,
      });
    });
  }

  private subscribe<Type>(observable: Observable<Type>, subscribe: (response: Type) => void, error?: () => void) {
    observable.subscribe(
      response => subscribe(response),
      err => {
        if(err.status === 401) {
          this.messageService.add({
            key: 'tr',
            severity: 'error',
            summary: this.translateService.instant('common.error'),
            detail: this.translateService.instant('auth.unauthorized'),
          });
          this.loginService.logout();
        }

        this.error(err.error);
        if (error) {
          error();
        }
      }
    );
  }

  public post<Type>(url: string, request: any, subscribe: (response: Type) => void, error?: () => void)
    : void {
    const observable
      = this.http.post<Type>(url, request, { headers: this.loginService.getHeaderWithBearer()})
    this.subscribe(observable, subscribe, error);
  }

  public get<Type>(url: string, subscribe: (response: Type) => void, error?: () => void): void {
    const observable
      = this.http.get<Type>(url, { headers: this.loginService.getHeaderWithBearer()});
    this.subscribe(observable, subscribe, error);
  }

  public delete<Type>(url: string, subscribe: (response: Type) => void, error?: () => void): void {
    const observable
      = this.http.delete<Type>(url, { headers: this.loginService.getHeaderWithBearer()});
    this.subscribe(observable, subscribe, error);
  }

  public put<Type>(url: string, request: any, subscribe: (response: Type) => void, error?: () => void): void {
    const observable
      = this.http.put<Type>(url, request, { headers: this.loginService.getHeaderWithBearer()});
    this.subscribe(observable, subscribe, error);
  }
}
