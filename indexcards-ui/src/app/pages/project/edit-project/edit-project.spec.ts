import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditProject } from './edit-project';

describe('EditProject', () => {
  let component: EditProject;
  let fixture: ComponentFixture<EditProject>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditProject],
    }).compileComponents();

    fixture = TestBed.createComponent(EditProject);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
