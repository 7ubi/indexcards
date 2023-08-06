import { Component } from '@angular/core';
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'indexcards-ui';
  lang: string = "en";

  constructor(
    public translate: TranslateService,
  ) {
    translate.addLangs(['en', 'de']);
    translate.setDefaultLang('en');

    const browserLang = translate.getBrowserLang();
    let lang = browserLang?.match(/en|de/) ? browserLang : 'en';
    this.changeLang(lang);
  }

  changeLang(lang: string) {
    this.translate.use(lang);
  }
}
