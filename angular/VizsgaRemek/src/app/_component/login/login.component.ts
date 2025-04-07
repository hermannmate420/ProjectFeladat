import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { FooterComponent } from '../footer/footer.component';
import { LoginService } from '../../services/login.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FooterComponent, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  showPassword: boolean = false;
  errorMessage: string = '';

  constructor(private fb: FormBuilder, private loginService: LoginService, private router: Router, private titleService: Title) {
    titleService.setTitle("Login");
  }

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  async onLogin(): Promise<void> {
    this.errorMessage = '';

    if (this.loginForm.invalid) {
      this.errorMessage = 'Please fill in all fields!';
      return;
    }

    try {
      const { email, password } = this.loginForm.value;
      await this.loginService.login(email, password);
      alert('Login successful!');
      this.router.navigate([this.loginService.getIsAdmin() ? '/admin' : '/home']);
    } catch (error) {
      console.error('Error logging in: ', error);
      this.checkReactivation(this.loginForm.value.email);
      alert('Error logging in: Invalid email or password!');

      console.log(this.loginForm.value.email)
    }
  }

  get email() {
    return this.loginForm.get('email');
  }

  get password() {
    return this.loginForm.get('password');
  }

  getEmailClass() {
    return this.email?.touched && this.email?.invalid ? 'invalid' : '';
  }

  getPasswordClass() {
    return this.password?.touched && this.password?.invalid ? 'invalid' : '';
  }

  checkReactivation(email: string): void {
    const reactivateCheckUrl = `http://localhost:8080/vintage_project-1.0-SNAPSHOT/webresources/user/reactivatable?email=${email}`;
    const reactivateRequestUrl = 'http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/user/reactivate-request';

    fetch(reactivateCheckUrl)
      .then(res => res.json())
      .then(data => {
        if (data.reactivatable) {
          // ✅ Igen, reaktiválható => küldjünk emailt
          return fetch(reactivateRequestUrl, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email })
          });
        } else {
          throw new Error('Not reactivatable');
        }
      })
      .then(response => {
        if (!response.ok) {
          throw new Error('Failed to send reactivation email.');
        }
        alert('Your account is currently inactive. A reactivation email has been sent.');
      })
      .catch(err => {
        console.error('Reactivation check or email error:', err);
        this.errorMessage = 'Login failed: invalid credentials or account is inactive.';
      });
  }


}
