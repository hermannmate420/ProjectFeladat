import { Component } from '@angular/core';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-admin-product-categories',
  imports: [],
  templateUrl: './admin-product-categories.component.html',
  styleUrl: './admin-product-categories.component.css'
})
export class AdminProductCategoriesComponent {
  constructor(private titleService: Title) {
    titleService.setTitle("Admin | Product Categories");
  }
}
