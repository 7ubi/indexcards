import { TestBed } from '@angular/core/testing';

import { SettingsService } from './settings.service';

describe('SettingsService', () => {
  let service: SettingsService;

  beforeEach(() => {
    localStorage.clear();
    service = TestBed.inject(SettingsService);
  });

  it('should show the due dialog by default', () => {
    expect(service.isDueDialogEnabled()).toBe(true);
  });

  it('should remember when the due dialog is disabled', () => {
    service.setDueDialogEnabled(false);

    expect(service.isDueDialogEnabled()).toBe(false);

    service.setDueDialogEnabled(true);

    expect(service.isDueDialogEnabled()).toBe(true);
  });
});
