import { Component } from '@angular/core';
import packageInfo from '../../../../../package.json';
@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent {
  public year: number = new Date().getFullYear();
  public version = packageInfo.version;
}
