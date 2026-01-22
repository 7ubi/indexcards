import {ApplicationConfig, inject, provideAppInitializer, provideBrowserGlobalErrorListeners} from '@angular/core';
import {provideRouter} from '@angular/router';

import {routes} from './app.routes';
import {provideHttpClient} from '@angular/common/http';
import {provideTranslateService, TranslateService} from '@ngx-translate/core';
import {provideTranslateHttpLoader} from '@ngx-translate/http-loader';
import {FormGroupDirective} from '@angular/forms';
import HttpService from './service/http/http.service';
import LocalService from './service/local/local.service';
import {LoginService} from './service/login/login.service';
import {LoginRequired} from './service/login/login-required';


export function appInitFactory() {
  return (): void => {
    const translate = inject(TranslateService);
    if (localStorage.getItem('language')) {
      const lang = localStorage.getItem('language');
      if (lang) {
        translate.use(lang);
        localStorage.setItem("language", lang)
      }
    } else {
      translate.addLangs(['en', 'de']);
      translate.use('en');
      localStorage.setItem('language', 'en');
    }

    const browserLang = translate.getBrowserLang();
    const lang = browserLang?.match(/en|de/) ? browserLang : 'en';
    translate.use(lang);
    localStorage.setItem("language", lang);
    console.log(lang);
  };
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(),
    FormGroupDirective,
    provideTranslateService({
      lang: 'en',
      fallbackLang: 'en',
      loader: provideTranslateHttpLoader({
        prefix: '/assets/i18n/',
        suffix: '.json'
      })
    }),
    TranslateService,
    HttpService,
    LocalService,
    LoginService,
    LoginRequired,
    provideAppInitializer(appInitFactory())
  ]
};
