import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {TranslateService} from "@ngx-translate/core";
import {Observable} from "rxjs";
import {MatSnackBar} from '@angular/material/snack-bar';
import {LoginService} from '../login/login.service';

@Injectable({
  providedIn: 'root'
})
class HttpService {

  private _snackBar = inject(MatSnackBar);

  constructor(
    private http: HttpClient,
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
        this._snackBar.open(`${this.translateService.instant(`common.error`)}: ${this.translateService.instant(`backend.${err.error}`)}`, 'Ok', {
          duration: 3000
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

export default HttpService
