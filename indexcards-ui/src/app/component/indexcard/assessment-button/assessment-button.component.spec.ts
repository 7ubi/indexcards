import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssessmentButtonComponent } from './assessment-button.component';

describe('AssessmentButtonComponent', () => {
  let component: AssessmentButtonComponent;
  let fixture: ComponentFixture<AssessmentButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AssessmentButtonComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AssessmentButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
