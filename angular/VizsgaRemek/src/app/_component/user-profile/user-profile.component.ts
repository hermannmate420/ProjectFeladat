import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { Title } from '@angular/platform-browser';
import { AdminUserTableComponent } from "../admin-user-table/admin-user-table.component";
import { RouterModule, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-user-profile',
  imports: [NavBarComponent, FormsModule, RouterModule],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css'
})
export class UserProfileComponent {
  sidebarOpen = false;
  
  constructor(private titleService: Title) {
          titleService.setTitle("Admin");
    }

    toggleSidebar() {
      this.sidebarOpen = !this.sidebarOpen;
    }
}
