import { Component } from '@angular/core';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { FooterComponent } from '../footer/footer.component';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../services/product.service';

@Component({
  selector: 'app-shop-layout',
  imports: [NavBarComponent, FooterComponent, RouterModule, FormsModule, CommonModule],
  templateUrl: './shop-layout.component.html',
  styleUrl: './shop-layout.component.css'
})
export class ShopLayoutComponent {

  constructor(private titleService: Title, private productService: ProductService) {
    titleService.setTitle("Shop");
  }


}