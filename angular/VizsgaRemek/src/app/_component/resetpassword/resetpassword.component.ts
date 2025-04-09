import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FooterComponent } from '../footer/footer.component';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-resetpassword',
  imports: [FooterComponent, FormsModule, ReactiveFormsModule, CommonModule],
  templateUrl: './resetpassword.component.html',
  styleUrl: './resetpassword.component.css'
})
export class ResetpasswordComponent {
  forgotForm: FormGroup;
  successMessage = '';
  errorMessage = '';

  constructor(private titleService: Title, private fb: FormBuilder, private http: HttpClient) {
    titleService.setTitle("Forgot Password");
    this.forgotForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  onSubmit() {
    if (this.forgotForm.valid) {
      const email = this.forgotForm.value.email;

      this.http.post('http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/user/forgot-password', {
        email
      }).subscribe({
        next: () => {
          this.successMessage = 'Reset link has been sent to your email!';
          this.errorMessage = '';
        },
        error: (err) => {
          console.error('Küldési hiba:', err);
          this.errorMessage = 'Something went wrong. Try again.';
          this.successMessage = '';
        }
      });
    }
  }

}
