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
            // Automatikus bejelentkeztetés a regisztrált adatokkal
            await this.loginService.login(formData.email, formData.password);
            console.log('Automatikus login sikeres.');

            // Email küldés - most már van token!
            this.emailService.sendEmail(
              formData.email,
              'Welcome to Vintage Shop',
              'welcome', // template name
              {
                name: formData.firstName,
                homelink: 'http://localhost:4200/home'
              }
            )
              .subscribe({
                next: () => console.log('Email elküldve'),
                
                error: (err) => console.error('Email küldési hiba:', err)
              });

            // Továbbléptetés home vagy más oldalra
            window.location.href = '/home'; // vagy amit szeretnél
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
  }
}