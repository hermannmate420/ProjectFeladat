import { Component } from '@angular/core';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { FooterComponent } from '../footer/footer.component';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { ShopAllComponent } from '../shop-all/shop-all.component';
import { Product } from '../../models/product.model';
import { ProductService } from '../../services/product.service';

@Component({
  selector: 'app-shop-layout',
  imports: [NavBarComponent, FooterComponent, RouterModule, FormsModule, CommonModule],
  templateUrl: './shop-layout.component.html',
  styleUrl: './shop-layout.component.css'
})
export class ShopLayoutComponent {
  sidebarOpen = false;
  filterText = '';
  selectedCategory = '';
  inStockOnly = false;
  categories: string[] = ['Órák', 'Ékszerek', 'Gyertyatartók', 'Táskák', 'Lemezjátszók', 'Bakelit Lemez Gyűjtemények', 'Lámpák', '', ''];
  sidebarItems = [
    { label: 'Home', icon: '🏠', link: 'homeAdmin' },
    { label: 'Users', icon: '👤', link: 'users' },
    { label: 'Products', icon: '📦', link: 'products' },
    { label: 'Orders', icon: '📦', link: 'orders' },
    { label: 'Newsletter', icon: '📧', link: 'newsletter' },
    { label: 'Messages', icon: '📬', link: 'messages' },
    { label: 'Analytics', icon: '📊', link: 'analytics' },
  ];

  constructor(private titleService: Title, private productService: ProductService) {
    titleService.setTitle("Shop");
  }


  toggleSidebar() {
    this.sidebarOpen = !this.sidebarOpen;
  }

  updateFilter() {
    this.productService.filterText$.next(this.filterText);
    this.productService.selectedCategory$.next(this.selectedCategory);
    this.productService.inStockOnly$.next(this.inStockOnly);
  }

  selectCategory(cat: string) {
    this.selectedCategory = cat;
    this.updateFilter();
  }

}