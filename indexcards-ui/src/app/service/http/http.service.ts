import {Injectable} from "@angular/core";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {Observable} from "rxjs";
import {LoginService} from '../login/login.service';
import {SnackbarService} from '../snackbar/snackbar.service';

@Injectable({
  providedIn: 'root'
})
class HttpService {

  constructor(
    private http: HttpClient,
    private loginService: LoginService,
    private snackbarService: SnackbarService
  ) {
  }

  private subscribe<Type>(observable: Observable<Type>, subscribe: (response: Type) => void, error?: () => void) {
    observable.subscribe({
      next: (response: Type) => subscribe(response),
      error: (err: HttpErrorResponse) => {
        if (err.status === 401 && err.error !== 'user_not_project_owner') {
          this.loginService.logout();
        }

        this.snackbarService.showHttpError(err);

        if (error) {
          error();
        }
      }
    });
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
