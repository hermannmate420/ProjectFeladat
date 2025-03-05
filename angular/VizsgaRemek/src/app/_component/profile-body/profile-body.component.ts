import { Component, OnInit } from '@angular/core';
import { LoginService } from '../../services/login.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-profile-body',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile-body.component.html',
  styleUrl: './profile-body.component.css'
})
export class ProfileBodyComponent implements OnInit {
  user: any = null;

  constructor(private loginService: LoginService) {}

  ngOnInit(): void {
    const userId = this.loginService.getUserId();
    if (userId) {
      this.loginService.getUserById(userId).subscribe({
        next: (data) => this.user = data,
        error: (err) => console.error('Error fetching user data', err)
      });
    }
  }
}