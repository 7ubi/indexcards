import {Injectable} from "@angular/core";
import {LoginService} from "../component/auth/login/login.service";
import {HttpClient} from "@angular/common/http";
import {ResultResponse} from "../app.response";
import {MessageService} from "primeng/api";
import {LangChangeEvent, TranslateService} from "@ngx-translate/core";

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

  private error(error: ResultResponse) {
    let summary = this.translateService.instant('common.error');
    this.translateService.get('common.error').subscribe((text: string) => {
      this.translateService.onLangChange.subscribe((event: LangChangeEvent) => {
        summary = this.translateService.instant(text);
      });
    });
    error.errorMessages.forEach(error => {
      this.messageService.add({
        key: 'tr',
        severity: 'error',
        summary: this.translateService.instant('common.error'),
        detail: error.message,
      });
    });
  }

  public post<Type>(url: string, request: any, subscribe: (response: Type) => void): void {
    this.http.post<Type>(url, request, { headers: this.loginService.getHeaderWithBearer()})
      .subscribe(
        response => subscribe(response)
        , err => this.error(err.error)
      );
  }

  public get<Type>(url: string, subscribe: (response: Type) => void, error?: () => void): void {
    this.http.get<Type>(url, { headers: this.loginService.getHeaderWithBearer()})
      .subscribe(
        response => subscribe(response),
        err => {
          this.error(err.error)
          if (error) {
            error();
          }
        }
      );
  }
}
