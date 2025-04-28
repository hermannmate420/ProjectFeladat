import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { map, switchMap } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { NewsletterSubscriber } from '../models/newsletter-subscriber.model';

@Injectable({
  providedIn: 'root'
})
export class EmailService {
  private emailUrl = 'http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/user/send';
  private newsletterSUrl = 'http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/newsletterSubscriber';
  private newsletterUrl = 'http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/newsletter';

  constructor(private http: HttpClient) { }


  sendEmail(to: string, subject: string, template: string, placeholders: any) {
    const token = sessionStorage.getItem('token');
    if (!token) {
      throw new Error('Nincs token a sessionStorage-ben!');
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

  sendWelcome(body: { to: string; name: string; template: string; subject: string }) {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });

    return this.http.post(this.newsletterUrl + '/send', body, { headers });
  }

  subscribeTo(body: { email: string; fullName: string }): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });

    const welcomeBody = {
      to: body.email,
      name: body.fullName,
      template: 'welcome-newletter',
      subject: 'Thank you for your subscription'
    };

    return this.http.post(this.newsletterSUrl + '/subscribe', body, { headers }).pipe(
      switchMap(() => this.sendWelcome(welcomeBody))
    );
  }

  getAllSubscribers(): Observable<NewsletterSubscriber[]> {
    return this.http.get<{ result: NewsletterSubscriber[] }>(this.newsletterSUrl + '/getAll').pipe(
      map(response => response.result)
    );
  }

  sendNewsletterEmail(body: {
    to: string;
    subject: string;
    template: string;
    name: string;
    mainContent: string;
    content: string;
    otherContent: string;
    homelink: string;
    currentYear: string;
  }) {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post(`${this.newsletterUrl}/send`, body, { headers });
  }

  unsubscribeNewsletter(id: number) {
    const headers = { 'Content-Type': 'application/json' };
    return this.http.delete(this.newsletterSUrl + '/unsubscribe?subscriberId=' + id, { headers });
  }
  


}
