import { Component } from '@angular/core';
import { EmailService } from '../../services/email.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-unsubscribe',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './unsubscribe.component.html',
  styleUrl: './unsubscribe.component.css'
})
export class UnsubscribeComponent {
  email = '';
  emailId = 0;
  message = '';
  isError = false;

  constructor(private emailService: EmailService, private titleService: Title) {
    titleService.setTitle('Unsbscribe')
  }

  unsubscribe(): void {
    if (!this.email || !this.email.includes('@')) {
      this.isError = true;
      this.message = '❌ Kérlek adj meg egy érvényes email címet!';
      return;
    }

    this.emailService.getAllSubscribers().subscribe({
      next: (res) => {
        const subscriber = res.find(sub => sub.email === this.email);
        if (subscriber) {
          const subscriberId = subscriber.subscriberId;
          console.log('📩 Leiratkozó ID:', subscriberId);

          this.emailService.unsubscribeNewsletter(subscriberId).subscribe({
            next: () => {
              this.isError = false;
              this.message = `✅ Sikeresen leiratkoztál a hírlevélről: ${this.email}`;
              this.email = '';
            },
            error: (err) => {
              this.isError = true;
              this.message = '❌ Hiba történt a leiratkozás során.';
              console.error(err);
            }
          });
        } else {
          this.isError = true;
          this.message = '⚠️ Nem találtunk ilyen email címet a feliratkozók között.';
        }
      },
      error: (err) => {
        this.isError = true;
        this.message = '❌ Nem sikerült betölteni a feliratkozókat.';
        console.error(err);
      }
    });
  }

}
