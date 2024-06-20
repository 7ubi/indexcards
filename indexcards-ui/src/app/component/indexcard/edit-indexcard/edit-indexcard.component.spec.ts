import {ComponentFixture, TestBed} from '@angular/core/testing';

import {EditIndexcardComponent} from './edit-indexcard.component';

describe('EditIndexcardComponent', () => {
  let component: EditIndexcardComponent;
  let fixture: ComponentFixture<EditIndexcardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditIndexcardComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(EditIndexcardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
