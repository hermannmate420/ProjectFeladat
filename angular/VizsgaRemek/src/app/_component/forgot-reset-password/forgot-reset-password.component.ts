import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';

@Component({
  selector: 'app-forgot-reset-password',
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './forgot-reset-password.component.html',
  styleUrl: './forgot-reset-password.component.css'
})
export class ForgotResetPasswordComponent implements OnInit {
  token: string = '';
  userId: number = 0;
  newPassword: string = '';
  confirmPassword: string = '';
  successMessage: string = '';

  countdown: number = 3;
  errorMessage: string = '';

  constructor(private route: ActivatedRoute, private http: HttpClient, private router: Router) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.token = params['token'];
      if (!this.token) {
        alert('Token not found in URL!');
        return;
      }

      try {
        const decoded: any = jwtDecode(this.token);
        this.userId = +decoded?.id || 0;
        if (!this.userId) {
          alert('Invalid token payload!');
        }
      } catch (e) {
        console.error('Token decoding failed:', e);
        alert('Invalid token format!');
      }
    });
  }

  changePassword(): void {
    if (!this.userId || !this.token) {
      alert('Missing user ID or token!');
      return;
    }

    if (!this.newPassword || !this.confirmPassword) {
      alert('Please fill in both password fields.');
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      alert('Passwords do not match.');
      return;
    }

    const passwordRegex = /^(?=.*[!@#$%^&*(),.?":{}|<>]).{8,}$/;
    if (!passwordRegex.test(this.newPassword)) {
      alert('Password must be at least 8 characters and include at least one special character.');
      return;
    }

    const payload = {
      userId: this.userId,
      newPassword: this.newPassword
    };

    const headers: HttpHeaders = new HttpHeaders({
      'Content-Type': 'application/json',
      'token': this.token
    });

    this.http.put(
      'http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/user/changePassword',
      payload,
      { headers }
    ).subscribe({
      next: () => {
        this.successMessage = '✅ Password successfully changed!';
        this.countdown = 3;
        this.errorMessage = '';
        this.newPassword = '';
        this.confirmPassword = '';

        const interval = setInterval(() => {
          this.countdown--;
          if (this.countdown === 0) {
            clearInterval(interval);
            this.router.navigate(['/login']);
          }
        }, 1000);
      },
      error: (err) => {
        console.error('Password change error:', err);
        this.errorMessage = '❌ Error updating password.';
        alert('Failed to change password.');
      }
    });
  }
}
