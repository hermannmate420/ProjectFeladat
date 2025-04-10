import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FooterComponent } from '../footer/footer.component';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { CartBodyComponent } from "../cart-body/cart-body.component";
import { CartProductListComponent } from "../cart-product-list/cart-product-list.component";
import { Title } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-cart',
  imports: [FormsModule, FooterComponent, NavBarComponent, CartBodyComponent, CartProductListComponent, CommonModule],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.css'
})
export class CartComponent {
  cartItems: any[] = [];
  constructor(private titleService: Title) {
    titleService.setTitle("Cart");
  }

}
