import {Component, OnInit} from '@angular/core';
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'indexcards-ui';

  constructor(
    public translate: TranslateService
  ) {

  }

  ngOnInit(): void {
    if (localStorage.getItem('language')) {
      const lang = localStorage.getItem('language');
      if (lang) {
        this.changeLang(lang);
      }
    } else {
      this.translate.addLangs(['en', 'de']);
      this.translate.setDefaultLang('en');
      localStorage.setItem('language', 'en');
    }

    const browserLang = this.translate.getBrowserLang();
    const lang = browserLang?.match(/en|de/) ? browserLang : 'en';
    this.changeLang(lang);
  }



  changeLang(lang: string) {
    this.translate.use(lang);
    localStorage.setItem("language", lang);
  }
}
