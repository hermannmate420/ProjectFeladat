import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { Title } from '@angular/platform-browser';
import { AdminUserTableComponent } from "../admin-user-table/admin-user-table.component";
import { RouterModule, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-user-profile',
  imports: [NavBarComponent, FormsModule, RouterModule, CommonModule],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css'
})
export class UserProfileComponent {
  sidebarOpen = false;
  sidebarItems = [
    { label: 'Home', icon: '🏠', link: 'homeAdmin' },
    { label: 'Users', icon: '👤', link: 'users' },
    { label: 'Products', icon: '📦', link: 'products' },
    { label: 'Orders', icon: '📦', link: 'orders' },
    { label: 'Newsletter', icon: '📧', link: 'newsletter' },
    { label: 'Messages', icon: '📬', link: 'messages' },
    { label: 'Analytics', icon: '📊', link: 'analytics' },
  ];


  constructor(private titleService: Title) {
    titleService.setTitle("Admin");
  }

  toggleSidebar() {
    this.sidebarOpen = !this.sidebarOpen;
  }
}
