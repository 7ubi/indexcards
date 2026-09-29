import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BarChart } from './bar-chart';

describe('BarChart', () => {
  let component: BarChart;
  let fixture: ComponentFixture<BarChart>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BarChart],
    }).compileComponents();

    fixture = TestBed.createComponent(BarChart);
    component = fixture.componentInstance;
  });

  it('should scale bars relative to the largest value', () => {
    fixture.componentRef.setInput('bars', [
      { label: '01.09.', value: 0 },
      { label: '02.09.', value: 5 },
      { label: '03.09.', value: 10 },
    ]);
    fixture.detectChanges();

    expect(component.max).toBe(10);
    const bars = fixture.nativeElement.querySelectorAll('.bar') as NodeListOf<HTMLElement>;
    expect(bars.length).toBe(3);
    expect(bars[0].style.height).toBe('0%');
    expect(bars[1].style.height).toBe('50%');
    expect(bars[2].style.height).toBe('100%');
    expect(fixture.nativeElement.querySelector('.x-axis').textContent).toContain('01.09.');
  });

  it('should render flat bars when all values are zero', () => {
    fixture.componentRef.setInput('bars', [{ label: '01.09.', value: 0 }]);
    fixture.detectChanges();

    expect(component.max).toBe(0);
    expect(component.height({ label: '01.09.', value: 0 })).toBe(0);
  });
});
