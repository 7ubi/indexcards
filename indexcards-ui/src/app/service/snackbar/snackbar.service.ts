import {Injectable} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {MatSnackBar} from '@angular/material/snack-bar';
import {HttpErrorResponse} from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class SnackbarService {

  constructor(private snackBar: MatSnackBar, private translateService: TranslateService) {
  }

  /**
   * Display error snackbar for a given http request error.
   * @param err
   */
  public showHttpError(err: HttpErrorResponse): void {
    this.snackBar.open(`${this.translateService.instant(`common.error`)}: ${this.translateService.instant(`backend.${err.error}`)}`, 'Ok', {
      duration: 3000
    });
  }

  /**
   * Display error snackbar for a given translation key.
   * @param key
   */
  public showErrorMessage(key: string): void {
    this.snackBar.open(this.translateService.instant(`common.error`) + ': ' + this.translateService.instant(key), 'Ok', {
      duration: 3000
    });
  }

  /**
   * Display success snackbar for a given translation key.
   * @param key
   */
  public showSuccessMessage(key: string): void {
    this.snackBar.open(this.translateService.instant(`common.success`) + ': ' + this.translateService.instant(key), 'Ok', {
      duration: 3000
    });
  }

  /**
   * Display warn snackbar for a given translation key.
   * @param key
   */
  public showWarnMessage(key: string): void {
    this.snackBar.open(this.translateService.instant(`common.warn`) + ': ' + this.translateService.instant(key), 'Ok', {
      duration: 3000
    });
  }
}
