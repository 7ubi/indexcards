import { Injectable } from '@angular/core';

/**
 * Per-browser user preferences. Kept in localStorage (like the language) so they survive a logout, unlike the
 * token in sessionStorage.
 */
@Injectable({
  providedIn: 'root',
})
export class SettingsService {
  private static readonly DUE_DIALOG_KEY = 'showDueDialog';

  public isDueDialogEnabled(): boolean {
    return localStorage.getItem(SettingsService.DUE_DIALOG_KEY) !== 'false';
  }

  public setDueDialogEnabled(enabled: boolean): void {
    localStorage.setItem(SettingsService.DUE_DIALOG_KEY, String(enabled));
  }
}
