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

  constructor(private fb: FormBuilder, private loginService: LoginService, private router: Router, private titleService: Title ) {
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
      alert('Error logging in: Invalid email or password!');
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
}
