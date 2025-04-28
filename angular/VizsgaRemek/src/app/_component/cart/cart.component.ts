import { Component, ViewChild } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FooterComponent } from '../footer/footer.component';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { CartBodyComponent } from "../cart-body/cart-body.component";
import { CartProductListComponent } from "../cart-product-list/cart-product-list.component";
import { CartService } from '../../services/cart.service';
import { OrderService } from '../../services/order.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [FormsModule, FooterComponent, NavBarComponent, CartBodyComponent, CartProductListComponent, CommonModule],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.css'
})
export class CartComponent {
  @ViewChild(CartBodyComponent) cartBodyComponent!: CartBodyComponent;
  @ViewChild(CartProductListComponent) cartProductListComponent!: CartProductListComponent;

  constructor(
    private titleService: Title,
    private cartService: CartService,
    private orderService: OrderService
  ) {
    titleService.setTitle("Cart");
  }

  continueToCheckout(): void {
    if (!this.cartBodyComponent.isShippingAddressValid()) {
      alert("❌ Please fill out shipping details before proceeding.");
      return;
    }

    const userId = Number(sessionStorage.getItem('id'));

    if (!userId) {
      alert('❌ No user logged in!');
      return;
    }

    const orderRequest = {
      userId: userId,
      address: this.cartBodyComponent.shippingAddress,
      items: this.cartProductListComponent.cartItems.map(item => ({
        productId: item.product.id,
        quantity: item.quantity
      }))
    };
  }
}
