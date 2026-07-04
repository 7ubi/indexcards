import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditIndexcard } from './edit-indexcard';

describe('EditIndexcard', () => {
  let component: EditIndexcard;
  let fixture: ComponentFixture<EditIndexcard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditIndexcard],
    }).compileComponents();

    fixture = TestBed.createComponent(EditIndexcard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
