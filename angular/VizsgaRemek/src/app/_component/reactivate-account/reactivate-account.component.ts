import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-reactivate-account',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reactivate-account.component.html',
  styleUrl: './reactivate-account.component.css'
})
export class ReactivateAccountComponent implements OnInit {
  message = '';
  loading = true;
  redirectDelay = 5;
  missToken = true;

  constructor(private route: ActivatedRoute, private router: Router, private titleService: Title) { 
    titleService.setTitle('Reactivate accunt');
  }

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');
    if (token) {
      this.reactivateAccount(token);
      this.missToken = false;
    } else {
      this.message = 'Invalid or missing token!';
      this.missToken = true;
      this.loading = false;
    }
  }

  reactivateAccount(token: string): void {
    fetch(`http://localhost:8080/vintage_project-1.0-SNAPSHOT/webresources/user/reactivate-from-token?token=${token}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(res => {
        if (!res.ok) {
          throw new Error('Reactivation failed.');
        }
        return res.text();
      })
      .then(() => {
        this.message = '✅ Your account has been successfully reactivated!';
        this.loading = false;
        this.startRedirectCountdown();
      })
      .catch(error => {
        console.error('Reactivation error:', error);
        this.message = '❌ Reactivation failed. Please contact support.';
        this.missToken = true;
        this.loading = false;
      });
  }

  startRedirectCountdown(): void {
    const interval = setInterval(() => {
      this.redirectDelay--;
      if (this.redirectDelay === 0) {
        clearInterval(interval);
        this.router.navigate(['/login']);
      }
    }, 1000);
  }
}
