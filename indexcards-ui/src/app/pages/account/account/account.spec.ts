import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideTranslateService } from '@ngx-translate/core';
import { vi } from 'vitest';
import { MatSlideToggleChange } from '@angular/material/slide-toggle';

import { Account } from './account';
import HttpService from '../../../service/http/http.service';
import { LoginService } from '../../../service/login/login.service';
import { SnackbarService } from '../../../service/snackbar/snackbar.service';
import { UserResponse } from '../../../app.responses';
import { SettingsService } from '../../../service/settings/settings.service';

describe('Account', () => {
  let component: Account;
  let fixture: ComponentFixture<Account>;
  const user: UserResponse = {
    username: 'test',
    firstname: 'Max',
    surname: 'Muster',
    admin: false,
  };
  const httpService = {
    get: vi.fn((_url: string, subscribe: (response: UserResponse) => void) => subscribe(user)),
    put: vi.fn(),
    delete: vi.fn(),
  };
  const loginService = { logout: vi.fn() };
  const settingsService = { isDueDialogEnabled: vi.fn(() => true), setDueDialogEnabled: vi.fn() };
  const snackbarService = {
    showErrorMessage: vi.fn(),
    showSuccessMessage: vi.fn(),
    showWarnMessage: vi.fn(),
  };

  beforeEach(async () => {
    vi.clearAllMocks();

    await TestBed.configureTestingModule({
      imports: [Account],
      providers: [
        provideRouter([]),
        provideTranslateService(),
        { provide: HttpService, useValue: httpService },
        // Used by the embedded DeleteAccount component.
        { provide: LoginService, useValue: loginService },
        { provide: SnackbarService, useValue: snackbarService },
        { provide: SettingsService, useValue: settingsService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Account);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should load and show the user', () => {
    expect(httpService.get).toHaveBeenCalledWith('/api/user', expect.any(Function));
    expect(component.loading).toBe(false);
    expect(component.user).toEqual(user);
    expect(component.usernameFormGroup.get('username')?.value).toBe('test');
  });

  it('should not change the username when it is empty', () => {
    component.usernameFormGroup.setValue({ username: '' });

    component.changeUsername();

    expect(httpService.put).not.toHaveBeenCalled();
    expect(snackbarService.showErrorMessage).toHaveBeenCalledWith('auth.fields_required');
  });

  it('should not call the API when the username is unchanged', () => {
    component.usernameFormGroup.setValue({ username: ' test ' });

    component.changeUsername();

    expect(httpService.put).not.toHaveBeenCalled();
    expect(snackbarService.showWarnMessage).toHaveBeenCalledWith('account.username_not_changed');
  });

  it('should change the username without a password', () => {
    const renamed: UserResponse = { ...user, username: 'renamed' };
    httpService.put.mockImplementation(
      (_url: string, _request: unknown, subscribe: (response: UserResponse) => void) =>
        subscribe(renamed),
    );
    component.usernameFormGroup.setValue({ username: 'renamed' });

    component.changeUsername();

    expect(httpService.put).toHaveBeenCalledWith(
      '/api/user/username',
      { username: 'renamed' },
      expect.any(Function),
    );
    expect(component.user).toEqual(renamed);
    expect(component.usernameFormGroup.get('username')?.value).toBe('renamed');
    expect(snackbarService.showSuccessMessage).toHaveBeenCalledWith('account.username_changed');
  });

  it('should not offer a password change anymore', () => {
    const element = fixture.nativeElement as HTMLElement;

    expect(element.querySelector('app-password-input')).toBeNull();
    expect((component as unknown as Record<string, unknown>)['changePassword']).toBeUndefined();
  });

  it('should save the due dialog setting', () => {
    expect(component.showDueDialog).toBe(true);

    component.toggleDueDialog({ checked: false } as MatSlideToggleChange);

    expect(component.showDueDialog).toBe(false);
    expect(settingsService.setDueDialogEnabled).toHaveBeenCalledWith(false);
  });
});
