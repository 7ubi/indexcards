import { Component, ChangeDetectionStrategy } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-privacy',
  imports: [TranslatePipe],
  templateUrl: './privacy.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './privacy.css',
})
export class Privacy {
  // Order of the sections on the page; all texts live under `privacy.sections.<id>` in the i18n files.
  readonly sections = [
    'controller',
    'overview',
    'hosting',
    'account',
    'content',
    'browser_storage',
    'third_parties',
    'recipients',
    'retention',
    'security',
    'rights',
    'obligation',
    'changes',
  ];

  // Translation values for `paragraphs` / `items` are arrays; a missing key resolves to the key string.
  toLines(value: unknown): string[] {
    return Array.isArray(value) ? value.filter((line) => typeof line === 'string') : [];
  }
}
