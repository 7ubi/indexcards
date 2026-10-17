import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideTranslateService } from '@ngx-translate/core';
import { vi } from 'vitest';

import { DeleteAccount } from './delete-account';
import HttpService from '../../service/http/http.service';
import { LoginService } from '../../service/login/login.service';
import { SnackbarService } from '../../service/snackbar/snackbar.service';

describe('DeleteAccount', () => {
  let component: DeleteAccount;
  let fixture: ComponentFixture<DeleteAccount>;
  const httpService = { delete: vi.fn() };
  const loginService = { logout: vi.fn() };
  const snackbarService = { showErrorMessage: vi.fn(), showSuccessMessage: vi.fn() };

  beforeEach(async () => {
    vi.clearAllMocks();

    await TestBed.configureTestingModule({
      imports: [DeleteAccount],
      providers: [
        provideRouter([]),
        provideTranslateService(),
        { provide: HttpService, useValue: httpService },
        { provide: LoginService, useValue: loginService },
        { provide: SnackbarService, useValue: snackbarService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DeleteAccount);
    fixture.componentRef.setInput('username', 'test');
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not call the API when the typed username does not match', () => {
    component.deleteAccountFormGroup.setValue({ username: 'other' });

    component.deleteAccount();

    expect(httpService.delete).not.toHaveBeenCalled();
    expect(snackbarService.showErrorMessage).toHaveBeenCalledWith(
      'account.delete_username_mismatch',
    );
  });

  it('should not call the API without a typed username', () => {
    component.deleteAccount();

    expect(httpService.delete).not.toHaveBeenCalled();
    expect(snackbarService.showErrorMessage).toHaveBeenCalledWith(
      'account.delete_username_mismatch',
    );
  });

  it('should delete the account with the confirmed username and log out on success', () => {
    httpService.delete.mockImplementation((_url: string, subscribe: () => void) => subscribe());
    component.deleteAccountFormGroup.setValue({ username: ' test ' });

    component.deleteAccount();

    expect(httpService.delete).toHaveBeenCalledWith(
      '/api/user',
      expect.any(Function),
      expect.any(Function),
      { username: 'test' },
    );
    expect(loginService.logout).toHaveBeenCalled();
    expect(snackbarService.showSuccessMessage).toHaveBeenCalledWith('account.deleted');
  });

  it('should stay logged in and allow a retry when the deletion fails', () => {
    httpService.delete.mockImplementation(
      (_url: string, _subscribe: () => void, error: () => void) => error(),
    );
    component.deleteAccountFormGroup.setValue({ username: 'test' });

    component.deleteAccount();

    expect(loginService.logout).not.toHaveBeenCalled();
    expect(component.deleting()).toBe(false);
  });
});
