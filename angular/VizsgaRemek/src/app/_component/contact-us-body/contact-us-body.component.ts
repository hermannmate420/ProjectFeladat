import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-contact-us-body',
  imports: [FormsModule, ReactiveFormsModule, CommonModule],
  templateUrl: './contact-us-body.component.html',
  styleUrl: './contact-us-body.component.css'
})
export class ContactUsBodyComponent {
  contactForm: FormGroup;
  loading = false;
  successMessage = '';
  errorMessage = '';
  gdprAgreements = '';
  isHuman = '';
  clearMessages = {};


  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.contactForm = this.fb.group({
      name: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      subject: ['', Validators.required],
      message: ['', Validators.required],
      gdprAgreement: [false, Validators.requiredTrue],
      isHuman: [false, Validators.requiredTrue]
    });
  }

  onSubmit(): void {
    if (this.contactForm.invalid) return;

    const messageData = this.contactForm.value;
    this.clearMessages = {
      fullName: messageData.name,
      messageText: messageData.message,
      email: messageData.email,
      subject: messageData.subject
    }

    this.loading = true;
    this.successMessage = '';
    this.errorMessage = '';

    const formData = {
      ...this.contactForm.value,
      timestamp: new Date().toISOString()
    };


    this.http.post('http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/message/create', this.clearMessages)
      .subscribe({
        next: () => {
          this.successMessage = '✅ Message sent successfully!';
          this.contactForm.reset();
          this.loading = false;
        },
        error: (err) => {
          console.error('Error sending message:', err);
          this.errorMessage = '❌ Failed to send message.';
          this.loading = false;
        }
      });
  }

}
