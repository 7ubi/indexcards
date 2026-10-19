import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideTranslateService } from '@ngx-translate/core';
import { vi } from 'vitest';

import { TwoFactor } from './two-factor';
import HttpService from '../../service/http/http.service';
import { LoginService } from '../../service/login/login.service';
import { SnackbarService } from '../../service/snackbar/snackbar.service';
import { MfaDeviceResponse } from '../../app.responses';

describe('TwoFactor', () => {
  let component: TwoFactor;
  let fixture: ComponentFixture<TwoFactor>;
  const devices: MfaDeviceResponse[] = [
    { type: 'totp', id: '1', name: 'Phone', created: '2026-10-19T13:06:37Z', lastUsed: null },
    { type: 'webauthn', id: '1', name: 'YubiKey', created: '2026-10-18T10:00:00Z', lastUsed: null },
  ];
  const httpService = {
    get: vi.fn((_url: string, subscribe: (response: MfaDeviceResponse[]) => void) =>
      subscribe(devices),
    ),
    delete: vi.fn(),
  };
  const loginService = { getAuthentikUrl: vi.fn(() => 'https://auth.example.com') };
  const snackbarService = { showErrorMessage: vi.fn(), showSuccessMessage: vi.fn() };

  beforeEach(async () => {
    vi.clearAllMocks();

    await TestBed.configureTestingModule({
      imports: [TwoFactor],
      providers: [
        provideTranslateService(),
        { provide: HttpService, useValue: httpService },
        { provide: LoginService, useValue: loginService },
        { provide: SnackbarService, useValue: snackbarService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TwoFactor);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should load and show the devices', () => {
    expect(httpService.get).toHaveBeenCalledWith(
      '/api/user/mfa',
      expect.any(Function),
      expect.any(Function),
    );
    expect(component.loading()).toBe(false);
    const names = Array.from(
      (fixture.nativeElement as HTMLElement).querySelectorAll('.device-name'),
    ).map((element) => element.textContent?.trim());
    expect(names).toEqual(['Phone', 'YubiKey']);
  });

  it('should open the setup flow through the login flow', () => {
    const assign = vi.fn();
    vi.spyOn(window, 'location', 'get').mockReturnValue({ assign } as unknown as Location);

    component.setUp('webauthn');

    expect(assign).toHaveBeenCalledWith(
      'https://auth.example.com/if/flow/indexcards-authentication/?next=' +
        encodeURIComponent('/if/flow/indexcards-mfa-webauthn-setup/'),
    );
  });

  it('should show an error when the login settings are not loaded', () => {
    loginService.getAuthentikUrl.mockReturnValueOnce(undefined as unknown as string);

    component.setUp('totp');

    expect(snackbarService.showErrorMessage).toHaveBeenCalledWith('two_factor.setup_unavailable');
  });

  it('should remove only the chosen device', () => {
    httpService.delete.mockImplementation((_url: string, subscribe: () => void) => subscribe());

    component.remove(devices[1]);

    expect(httpService.delete).toHaveBeenCalledWith(
      '/api/user/mfa/webauthn/1',
      expect.any(Function),
      expect.any(Function),
    );
    expect(component.devices()).toEqual([devices[0]]);
    expect(component.removing()).toBeNull();
    expect(snackbarService.showSuccessMessage).toHaveBeenCalledWith('two_factor.removed');
  });

  it('should keep the device when removing failed', () => {
    httpService.delete.mockImplementation(
      (_url: string, _subscribe: () => void, error: () => void) => error(),
    );

    component.remove(devices[0]);

    expect(component.devices()).toEqual(devices);
    expect(component.removing()).toBeNull();
  });
});
