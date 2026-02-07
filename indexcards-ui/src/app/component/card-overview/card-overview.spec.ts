import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CardOverview } from './card-overview';

describe('CardOverview', () => {
  let component: CardOverview;
  let fixture: ComponentFixture<CardOverview>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CardOverview]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CardOverview);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
