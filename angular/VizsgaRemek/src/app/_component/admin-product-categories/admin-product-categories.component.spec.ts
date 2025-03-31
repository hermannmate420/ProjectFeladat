import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminProductCategoriesComponent } from './admin-product-categories.component';

describe('AdminProductCategoriesComponent', () => {
  let component: AdminProductCategoriesComponent;
  let fixture: ComponentFixture<AdminProductCategoriesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminProductCategoriesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminProductCategoriesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
