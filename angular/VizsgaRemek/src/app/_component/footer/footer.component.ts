import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { EmailService } from '../../services/email.service';


@Component({
  selector: 'app-footer',
  imports: [FormsModule, RouterModule],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.css'
})
export class FooterComponent {
  newsletterEmail: string = '';

  constructor(private emailService: EmailService) {

  }

  onSubmit(email: string) {
    if (!email || !email.includes('@')) {
      console.error('❌ Érvénytelen email cím!');
      return;
    }

    const fullName = email.split('@')[0].trim();

    this.emailService.subscribeTo({ email, fullName }).subscribe({
      next: () => console.log('✅ Sikeres feliratkozás és welcome email kiküldve!'),
      error: (err) => console.error('❌ Hiba történt:', err)
    });
  }



}
