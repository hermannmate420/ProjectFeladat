import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MypostsBodyComponent } from './myposts-body.component';

describe('MypostsBodyComponent', () => {
  let component: MypostsBodyComponent;
  let fixture: ComponentFixture<MypostsBodyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MypostsBodyComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MypostsBodyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
