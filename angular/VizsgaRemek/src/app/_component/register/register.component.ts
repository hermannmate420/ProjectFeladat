import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FooterComponent } from '../footer/footer.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { EmailService } from '../../services/email.service';
import { LoginService } from '../../services/login.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  imports: [FooterComponent, ReactiveFormsModule, FormsModule, CommonModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  registerForm: FormGroup;
  apiUrl = 'http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/user/registerUser';
  router: any;

  constructor(private fb: FormBuilder, private loginService: LoginService, private emailService: EmailService, private http: HttpClient, private titleService: Title) {
    titleService.setTitle("Registration");
    this.registerForm = this.fb.group({
      username: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: ['', [Validators.required, Validators.pattern('^(\\+?[0-9]{1,3})?[ -]?[0-9]{6,14}$')]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      iAgree: [false, Validators.requiredTrue]

    });
  }

  onSubmit() {
    if (this.registerForm.valid) {
      const formData = this.registerForm.value;

      this.http.post(this.apiUrl, formData).subscribe({
        next: async (response: any) => {
          console.log('Sikeres regisztráció:', response);
          alert('Sikeres regisztráció!');

          try {
            await this.loginService.login(formData.email, formData.password);
            console.log('Automatikus login sikeres.');
            this.emailService.sendEmail(
              formData.email,
              'Welcome to Vintage Shop',
              'welcome',
              {
                name: formData.firstName,
                homelink: 'http://localhost:4200/home'
              }
            )
              .subscribe({
                next: () => console.log('Email elküldve'),

                error: (err) => console.error('Email küldési hiba:', err)
              });

            window.location.href = '/home';
          } catch (error) {
            console.error('Automatikus login hiba:', error);
            alert('A bejelentkezés nem sikerült a regisztráció után.');
          }
        },
        error: (error) => {
          console.error('Regisztrációs hiba:', error);
          alert('Regisztráció sikertelen!');
        }
      });
    }
    this.subsForLetter(this.registerForm.value.email);
  }

  subsForLetter(email: string) {
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