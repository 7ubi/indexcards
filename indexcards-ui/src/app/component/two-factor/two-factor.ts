import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  computed,
  inject,
  signal,
} from '@angular/core';
import { DatePipe } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import HttpService from '../../service/http/http.service';
import { LoginService } from '../../service/login/login.service';
import { SnackbarService } from '../../service/snackbar/snackbar.service';
import { MfaDeviceResponse, MfaDeviceType } from '../../app.responses';

/** Authentik flows that set up a second factor (authentik/blueprints/indexcards.yaml). */
const SETUP_FLOWS: Record<MfaDeviceType, string> = {
  totp: 'indexcards-mfa-totp-setup',
  webauthn: 'indexcards-mfa-webauthn-setup',
  email: 'indexcards-mfa-email-setup',
  static: 'indexcards-mfa-static-setup',
};

const ICONS: Record<MfaDeviceType, string> = {
  totp: 'smartphone',
  webauthn: 'key',
  email: 'mail',
  static: 'password',
};

/**
 * Optional 2FA: lists the user's second factors and links to Authentik's setup flows. Setting one up happens in
 * Authentik, which returns to the account page afterwards; Authentik then asks for it on every login.
 */
@Component({
  selector: 'app-two-factor',
  imports: [DatePipe, TranslatePipe, MatButton, MatIconButton, MatIcon],
  templateUrl: './two-factor.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './two-factor.css',
})
export class TwoFactor implements OnInit {
  private httpService = inject(HttpService);
  private loginService = inject(LoginService);
  private snackbarService = inject(SnackbarService);

  readonly icons = ICONS;

  readonly devices = signal<MfaDeviceResponse[]>([]);

  /**
   * Recovery codes are offered once another factor is set up, and only one set at a time (remove it to get new codes).
   */
  readonly types = computed<MfaDeviceType[]>(() => {
    const devices = this.devices();
    const hasOtherFactor = devices.some((device) => device.type !== 'static');
    const hasRecoveryCodes = devices.some((device) => device.type === 'static');
    const types: MfaDeviceType[] = ['totp', 'webauthn', 'email'];
    return hasOtherFactor && !hasRecoveryCodes ? [...types, 'static'] : types;
  });

  readonly loading = signal(true);

  readonly removing = signal<string | null>(null);

  ngOnInit(): void {
    this.loadDevices();
  }

  private loadDevices(): void {
    this.httpService.get<MfaDeviceResponse[]>(
      '/api/user/mfa',
      (response) => {
        this.devices.set(response);
        this.loading.set(false);
      },
      () => this.loading.set(false),
    );
  }

  /**
   * Goes through the login flow first: it is skipped while the Authentik session is still active, otherwise the user
   * logs in again before the setup starts.
   */
  setUp(type: MfaDeviceType): void {
    const authentikUrl = this.loginService.getAuthentikUrl();
    if (!authentikUrl) {
      this.snackbarService.showErrorMessage('two_factor.setup_unavailable');
      return;
    }

    const next = encodeURIComponent(`/if/flow/${SETUP_FLOWS[type]}/`);
    window.location.assign(`${authentikUrl}/if/flow/indexcards-authentication/?next=${next}`);
  }

  remove(device: MfaDeviceResponse): void {
    if (this.removing()) {
      return;
    }

    this.removing.set(device.id);
    this.httpService.delete<undefined>(
      `/api/user/mfa/${device.type}/${encodeURIComponent(device.id)}`,
      () => {
        this.devices.update((devices) =>
          devices.filter((other) => other.type !== device.type || other.id !== device.id),
        );
        this.removing.set(null);
        this.snackbarService.showSuccessMessage('two_factor.removed');
      },
      () => this.removing.set(null),
    );
  }
}
