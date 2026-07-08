import {
  ApplicationConfig,
  inject,
  provideAppInitializer,
  provideBrowserGlobalErrorListeners,
} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient, withXhr } from '@angular/common/http';
import { provideTranslateService, TranslateService } from '@ngx-translate/core';
import { provideTranslateHttpLoader } from '@ngx-translate/http-loader';
import { FormGroupDirective } from '@angular/forms';
import { MAT_DATE_LOCALE, provideNativeDateAdapter } from '@angular/material/core';
import HttpService from './service/http/http.service';
import LocalService from './service/local/local.service';
import { LoginService } from './service/login/login.service';
import { LoginRequired } from './service/login/login-required';
import { SnackbarService } from './service/snackbar/snackbar.service';

const DD_MM_YYYY_DATE_FORMATS = {
  parse: {
    dateInput: { day: '2-digit', month: '2-digit', year: 'numeric' },
  },
  display: {
    dateInput: { day: '2-digit', month: '2-digit', year: 'numeric' },
    monthYearLabel: { year: 'numeric', month: 'short' },
    dateA11yLabel: { year: 'numeric', month: 'long', day: 'numeric' },
    monthYearA11yLabel: { year: 'numeric', month: 'long' },
  },
};

export function appInitFactory() {
  return (): void => {
    const translate = inject(TranslateService);
    if (localStorage.getItem('language')) {
      const lang = localStorage.getItem('language');
      if (lang) {
        translate.use(lang);
        localStorage.setItem('language', lang);
      }
    } else {
      translate.addLangs(['en', 'de']);
      translate.use('en');
      localStorage.setItem('language', 'en');
    }

    const browserLang = translate.getBrowserLang();
    const lang = browserLang?.match(/en|de/) ? browserLang : 'en';
    translate.use(lang);
    localStorage.setItem('language', lang);
  };
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withXhr()),
    FormGroupDirective,
    provideTranslateService({
      lang: 'en',
      fallbackLang: 'en',
      loader: provideTranslateHttpLoader({
        prefix: '/assets/i18n/',
        suffix: '.json',
      }),
    }),
    TranslateService,
    HttpService,
    LocalService,
    LoginService,
    SnackbarService,
    LoginRequired,
    provideAppInitializer(appInitFactory()),
    { provide: MAT_DATE_LOCALE, useValue: 'de-DE' },
    provideNativeDateAdapter(DD_MM_YYYY_DATE_FORMATS),
  ],
};
