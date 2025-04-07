import { Component, OnInit } from '@angular/core';
import { MessageService } from '../../services/message.service';
import { Message } from '../../models/message.model';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';
import { Ticket } from '../../models/ticket.model';

@Component({
  selector: 'app-admin-messages',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-messages.component.html',
  styleUrl: './admin-messages.component.css'
})
export class AdminMessagesComponent implements OnInit {
  messages: Message[] = [];
  ticket: Ticket[] = [];
  selectedTicket: Ticket | null = null;
  selectedMessage: Message | null = null;

  constructor(private messageService: MessageService, private userService: UserService) { }

  ngOnInit(): void {
    this.loadMessages();
    this.loadTicket();
  }

  loadMessages(): void {
    this.messageService.getMessage().subscribe({
      next: (res) => {
        console.log('Messages:', res);
        const rawMessages = res.result || res; 
        const feedbackMessages: Message[] = rawMessages.map((m: any) => ({
          id: m.messageId,
          name: m.fullName,
          email: m.email,
          subject: m.subject,
          message: m.messageText,
          createdAt: m.createdAt,
          isRead: false, // Default value for isRead
          type: 'feedback'
        }));
        this.messages = [...this.messages, ...feedbackMessages];
      },
      error: (err) => console.error('❌ Failed to load feedback messages', err)
    });
  }


  loadTicket(): void {
    this.userService.getAllTicket().subscribe({
      next: (res) => {
        const tickets = res.result || res;

        tickets.forEach((ticket: { userId: number; ticketId: any; ticketBody: any; createdAt: any; }) => {
          this.userService.getUserById(ticket.userId).subscribe({
            next: user => {
              this.ticket.push({
                id: ticket.ticketId,
                name: user.result.username,
                email: user.result.email,
                subject: `Ticket #${ticket.ticketId}`,
                message: ticket.ticketBody,
                createdAt: ticket.createdAt,
                isRead: false,
                type: 'ticket'
              });
            },
            error: err => console.error('❌ Nem sikerült lekérni a felhasználót:', err)
          });
        });
      },
      error: (err) => console.error('❌ Failed to load tickets', err)
    });
  }


  viewMessage(msg: Message): void {
    this.selectedMessage = msg;
    if (!msg.isRead) {
      this.messageService.markAsRead(msg.id).subscribe(() => msg.isRead = true);
    }
  }

  viewTicket(tck: Ticket): void {
    this.selectedTicket = tck;
    if (!tck.isRead) {
      this.messageService.markAsRead(tck.id).subscribe(() => tck.isRead = true);
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
