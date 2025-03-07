import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { LoginService } from '../../services/login.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-nav-bar',
  imports: [FormsModule, CommonModule],
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.css'
})
export class NavBarComponent {
  isAuthenticated = false;
  navIsAdmin = false;

  
  constructor(private loginService: LoginService) {
    this.navIsAdmin = this.loginService.getIsAdmin();
    this.loginService.authenticated$.subscribe(auth => {
      this.isAuthenticated = auth;
    });
  }

  onLogOut(): void {
    this.loginService.logOut();
    window.location.href = '/login';
  }
}
