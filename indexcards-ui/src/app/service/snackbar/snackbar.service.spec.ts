import { TestBed } from '@angular/core/testing';

import { provideTranslateService } from '@ngx-translate/core';

import { SnackbarService } from './snackbar.service';

describe('SnackbarService', () => {
  let service: SnackbarService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideTranslateService()] });
    service = TestBed.inject(SnackbarService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
