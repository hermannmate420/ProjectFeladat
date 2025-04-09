import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContactUsBodyComponent } from './contact-us-body.component';

describe('ContactUsBodyComponent', () => {
  let component: ContactUsBodyComponent;
  let fixture: ComponentFixture<ContactUsBodyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContactUsBodyComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ContactUsBodyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
