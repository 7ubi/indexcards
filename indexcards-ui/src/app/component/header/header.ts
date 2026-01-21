import {Component} from '@angular/core';
import {MatToolbar} from '@angular/material/toolbar';

@Component({
  selector: 'app-header',
  imports: [
    MatToolbar
  ],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {

}
