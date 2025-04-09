import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { LoginService } from '../../services/login.service';
import { CommonModule } from '@angular/common';
import { DarkModeService } from '../../services/dark-mode.service';
import e from 'express';
import { MessageService } from '../../services/message.service';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-nav-bar',
  imports: [FormsModule, CommonModule],
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.css'
})
export class NavBarComponent implements OnInit {
  isAuthenticated = false;
  navIsAdmin = false;
  cartItems: any[] = [];
  messageCount: number = 0;

  constructor(private loginService: LoginService, public darkModeService: DarkModeService, private messageService: MessageService, private userService: UserService) {
    this.navIsAdmin = this.loginService.getIsAdmin();
    this.loginService.authenticated$.subscribe(auth => {
      this.isAuthenticated = auth;
    });
  }

  ngOnInit(): void {
    this.loginService.authenticated$.subscribe(auth => {
      this.isAuthenticated = auth;

      if (this.loginService.getIsAdmin()) {
        this.fetchMessagesAndTickets(); // csak adminnál
      }
    });
  }

  fetchMessagesAndTickets(): void {
    this.messageService.getMessage().subscribe({
      next: (res) => {
        const feedbacks = res.result || res;
        const unreadMessages = feedbacks.filter((m: any) => !m.isRead).length;

        this.userService.getAllTicket().subscribe({
          next: (tickets) => {
            const openOrPending = tickets.result.filter((t: any) =>
              t.status === 'Open' || t.status === 'Pending'
            ).length;

            this.messageCount = unreadMessages + openOrPending;
          },
          error: err => console.error('❌ Ticket fetch error:', err)
        });
      },
      error: err => console.error('❌ Message fetch error:', err)
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
