import {Component, OnInit} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
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
      this.translate.use('en');
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
