import { Component, Input, OnChanges, ChangeDetectionStrategy } from '@angular/core';

export interface Bar {
  label: string;
  value: number;
}

@Component({
  selector: 'app-bar-chart',
  standalone: true,
  templateUrl: './bar-chart.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './bar-chart.css',
})
export class BarChart implements OnChanges {
  @Input({ required: true })
  bars: Bar[] = [];

  @Input()
  color = 'var(--mat-sys-primary)';

  max = 0;

  ngOnChanges(): void {
    this.max = this.bars.reduce((max, bar) => Math.max(max, bar.value), 0);
  }

  /** Bar height in percent of the chart; non-zero values stay visible. */
  height(bar: Bar): number {
    if (this.max === 0 || bar.value === 0) {
      return 0;
    }
    return Math.max((bar.value / this.max) * 100, 2);
  }
}
