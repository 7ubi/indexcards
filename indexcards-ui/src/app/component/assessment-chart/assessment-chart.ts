import { Component, Input, OnChanges, ChangeDetectionStrategy } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';

export interface AssessmentSlice {
  label: string;
  color: string;
  count: number;
}

@Component({
  selector: 'app-assessment-chart',
  standalone: true,
  imports: [TranslatePipe],
  templateUrl: './assessment-chart.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './assessment-chart.css',
})
export class AssessmentChart implements OnChanges {
  @Input({ required: true })
  slices: AssessmentSlice[] = [];

  total = 0;

  chartBackground = '#e0e0e0';

  ngOnChanges(): void {
    this.total = this.slices.reduce((sum, slice) => sum + slice.count, 0);

    if (this.total === 0) {
      this.chartBackground = '#e0e0e0';
      return;
    }

    let cumulative = 0;
    const stops: string[] = [];
    for (const slice of this.slices) {
      if (slice.count === 0) {
        continue;
      }
      const start = (cumulative / this.total) * 360;
      cumulative += slice.count;
      const end = (cumulative / this.total) * 360;
      stops.push(`${slice.color} ${start}deg ${end}deg`);
    }

    this.chartBackground = `conic-gradient(${stops.join(', ')})`;
  }
}
