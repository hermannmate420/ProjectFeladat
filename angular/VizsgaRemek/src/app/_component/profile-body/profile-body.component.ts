import { Component, OnInit } from '@angular/core';
import { LoginService } from '../../services/login.service';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-profile-body',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile-body.component.html',
  styleUrl: './profile-body.component.css'
})
export class ProfileBodyComponent implements OnInit {
  userData: any;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    const userId = localStorage.getItem('id');
    if (userId) {
      this.getUserData(Number(userId)); // Példa userId
    } else {
      console.error('User ID not found in localStorage');
    }
  }

  getUserData(userId: number): void {
    this.userService.getUserById(userId).subscribe({
      next: (data) => {
        this.userData = data;
        console.log('User Data:', this.userData);
      },
      error: (error) => {
        console.error('Error fetching user data:', error);
      }
    });
  }
}