import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class EmailService {
  private emailUrl = 'http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/user/send';

  constructor(private http: HttpClient) { }

  sendEmail(to: string, subject: string, template: string, placeholders: any) {
    const token = localStorage.getItem('token');
    if (!token) {
      throw new Error('Nincs token a localStorage-ben!');
    }

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'token': token
    });

    const body = {
      to,
      subject,
      template,
      placeholders
    };

    return this.http.post(this.emailUrl, body, { headers });
  }
}
