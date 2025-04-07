import { Component, OnInit } from '@angular/core';
import { MessageService } from '../../services/message.service';
import { Message } from '../../models/message.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-messages',
  imports: [CommonModule],
  templateUrl: './admin-messages.component.html',
  styleUrl: './admin-messages.component.css'
})
export class AdminMessagesComponent implements OnInit {
  messages: Message[] = [];
  selectedMessage: Message | null = null;

  constructor(private messageService: MessageService) { }

  ngOnInit(): void {
    this.loadMessages();
    this.messages = [
      {
        id: 1,
        name: 'John Doe',
        email: 'john@example.com',
        subject: 'Question about a product',
        message: 'Hi, I wanted to ask about the retro vinyl you have in stock...',
        createdAt: '2025-03-31T09:30:00Z',
        isRead: false
      },
      {
        id: 2,
        name: 'Jane Smith',
        email: 'jane@domain.com',
        subject: 'Issue with order',
        message: 'Hello, I have not received my order from last week.',
        createdAt: '2025-03-30T18:45:00Z',
        isRead: true
      }
    ];
  }

  loadMessages(): void {
    this.messageService.getMessages().subscribe({
      next: (res) => this.messages = res,
      error: (err) => console.error('❌ Failed to load messages', err)
    });
  }

  viewMessage(msg: Message): void {
    this.selectedMessage = msg;
    if (!msg.isRead) {
      this.messageService.markAsRead(msg.id).subscribe(() => msg.isRead = true);
    }
  }

  deleteMessage(id: number): void {
    if (confirm('Are you sure you want to delete this message?')) {
      this.messageService.deleteMessage(id).subscribe(() => {
        this.messages = this.messages.filter(m => m.id !== id);
        this.selectedMessage = null;
      });
    }
  }
}
