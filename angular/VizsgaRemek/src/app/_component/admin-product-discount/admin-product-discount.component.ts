import { Component } from '@angular/core';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-admin-product-discount',
  imports: [],
  templateUrl: './admin-product-discount.component.html',
  styleUrl: './admin-product-discount.component.css'
})
export class AdminProductDiscountComponent {
  constructor(private titleService: Title) {
    titleService.setTitle("Admin | Product Discount");
  }
}
