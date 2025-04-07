import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Message } from '../models/message.model';

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  private baseUrl = 'http://localhost:8080/vintage_project-1.0-SNAPSHOT/webresources/messages';

  constructor(private http: HttpClient) { }

  getMessages(): Observable<Message[]> {
    return this.http.get<Message[]>(`${this.baseUrl}/getAll`);
  }

  markAsRead(id: number): Observable<any> {
    return this.http.put(`${this.baseUrl}/markAsRead/${id}`, {});
  }

  deleteMessage(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/delete/${id}`);
  }
}
