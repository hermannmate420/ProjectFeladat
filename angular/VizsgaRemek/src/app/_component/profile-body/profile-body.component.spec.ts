import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfileBodyComponent } from './profile-body.component';

describe('ProfileBodyComponent', () => {
  let component: ProfileBodyComponent;
  let fixture: ComponentFixture<ProfileBodyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProfileBodyComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProfileBodyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
