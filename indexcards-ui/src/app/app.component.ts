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
    public translate: TranslateService,
  ) {}

  async changeLang(lang: string) {
    await this.translate.use(lang).toPromise();
  }

  async ngOnInit() {
    this.translate.addLangs(['en', 'de']);
    this.translate.setDefaultLang('en');

    const browserLang = this.translate.getBrowserLang();
    const lang = browserLang?.match(/en|de/) ? browserLang : 'en';
    await this.changeLang(lang);
  }
}
