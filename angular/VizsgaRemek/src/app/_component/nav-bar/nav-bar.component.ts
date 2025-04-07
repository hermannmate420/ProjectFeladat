import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { LoginService } from '../../services/login.service';
import { CommonModule } from '@angular/common';
import { DarkModeService } from '../../services/dark-mode.service';
import e from 'express';

@Component({
  selector: 'app-nav-bar',
  imports: [FormsModule, CommonModule],
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.css'
})
export class NavBarComponent {
  isAuthenticated = false;
  navIsAdmin = false;
  cartItems: any[] = [];

  constructor(private loginService: LoginService, public darkModeService: DarkModeService) {
    this.navIsAdmin = this.loginService.getIsAdmin();
    this.loginService.authenticated$.subscribe(auth => {
      this.isAuthenticated = auth;
    });
  }

  getCartItems(): any[] {
    const cartItems = localStorage.getItem('cart');
    return cartItems ? JSON.parse(cartItems) : [];
  }

  get totalItems(): number {
    const cart = this.getCartItems();
    return cart.reduce((sum, item) => sum + (item.quantity || 1), 0);
  }


  onLogOut(): void {
    this.loginService.logOut();
    window.location.href = '/login';
  }

  toggleTheme(): void {
    this.darkModeService.toggleDarkMode();
  }
}
