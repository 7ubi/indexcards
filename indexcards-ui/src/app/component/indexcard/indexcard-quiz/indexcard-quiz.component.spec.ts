import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IndexcardQuizComponent } from './indexcard-quiz.component';

describe('IndexcardQuizComponent', () => {
  let component: IndexcardQuizComponent;
  let fixture: ComponentFixture<IndexcardQuizComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ IndexcardQuizComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(IndexcardQuizComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
