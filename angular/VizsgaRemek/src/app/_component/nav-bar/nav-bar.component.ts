import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { LoginService } from '../../services/login.service';
import { CommonModule } from '@angular/common';
import { DarkModeService } from '../../services/dark-mode.service';

@Component({
  selector: 'app-nav-bar',
  imports: [FormsModule, CommonModule],
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.css'
})
export class NavBarComponent {
  isAuthenticated = false;
  navIsAdmin = false;

  
  constructor(private loginService: LoginService, public darkModeService: DarkModeService) {
    this.navIsAdmin = this.loginService.getIsAdmin();
    this.loginService.authenticated$.subscribe(auth => {
      this.isAuthenticated = auth;
    });
  }

  onLogOut(): void {
    this.loginService.logOut();
    window.location.href = '/login';
  }

  toggleTheme(): void {
    this.darkModeService.toggleDarkMode();
  }
}
