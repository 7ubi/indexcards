import {Injectable} from "@angular/core";
import {LoginService} from "../component/auth/login/login.service";
import {HttpClient} from "@angular/common/http";
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
  ) {
  }

  private subscribe<Type>(observable: Observable<Type>, subscribe: (response: Type) => void, error?: () => void) {
    observable.subscribe(
      response => subscribe(response),
      err => {
        if (err.status === 401 && err.error !== 'user_not_project_owner') {
          this.loginService.logout();
        }
        
        this.messageService.add({
          key: 'tr',
          severity: 'error',
          summary: this.translateService.instant('common.error'),
          detail: this.translateService.instant(`backend.${err.error}`)
        });

        if (error) {
          error();
        }
      }
    );
  }

  public post<Type>(url: string, request: any, subscribe: (response: Type) => void, error?: () => void)
    : void {
    const observable
      = this.http.post<Type>(url, request, {headers: this.loginService.getHeaderWithBearer()})
    this.subscribe(observable, subscribe, error);
  }

  public get<Type>(url: string, subscribe: (response: Type) => void, error?: () => void): void {
    const observable
      = this.http.get<Type>(url, {headers: this.loginService.getHeaderWithBearer()});
    this.subscribe(observable, subscribe, error);
  }

  public delete<Type>(url: string, subscribe: (response: Type) => void, error?: () => void, request?: any): void {
    const observable
      = this.http.delete<Type>(url, {headers: this.loginService.getHeaderWithBearer(), body: request});
    this.subscribe(observable, subscribe, error);
  }

  public put<Type>(url: string, request: any, subscribe: (response: Type) => void, error?: () => void): void {
    const observable
      = this.http.put<Type>(url, request, {headers: this.loginService.getHeaderWithBearer()});
    this.subscribe(observable, subscribe, error);
  }
}
