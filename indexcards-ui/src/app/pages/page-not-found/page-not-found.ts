import { Component, ChangeDetectionStrategy } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-page-not-found',
  imports: [TranslatePipe],
  templateUrl: './page-not-found.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './page-not-found.css',
})
export class PageNotFound {}
