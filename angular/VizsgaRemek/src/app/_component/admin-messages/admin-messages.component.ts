import { Component, OnInit } from '@angular/core';
import { MessageService } from '../../services/message.service';
import { Message } from '../../models/message.model';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';
import { Ticket } from '../../models/ticket.model';
import { Title } from '@angular/platform-browser';
import { userInfo } from 'os';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-admin-messages',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-messages.component.html',
  styleUrl: './admin-messages.component.css'
})
export class AdminMessagesComponent implements OnInit {
  messages: Message[] = [];
  ticket: Ticket[] = [];
  selectedTicket: Ticket | null = null;
  selectedMessage: Message | null = null;
  ticketFilterStatus: string = 'all';
  showOnlyOwnTickets: boolean = false;
  currentUserId: number = Number(localStorage.getItem('id'));
  newTicketText: string = '';
  isCreatingTicket: boolean = false;
  isReplying = false;
  replyText = '';


  constructor(private messageService: MessageService, private userService: UserService, private titleServices: Title) {
    titleServices.setTitle("Admin | Messages");
  }

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

        tickets.forEach((ticket: { userId: number; ticketId: any; ticketBody: any; createdAt: any; status: string; }) => {
          this.userService.getUserById(ticket.userId).subscribe({
            next: user => {
              this.ticket.push({
                id: ticket.ticketId,
                userId: ticket.userId,
                name: user.result.username,
                email: user.result.email,
                subject: `Ticket #${ticket.ticketId}`,
                message: ticket.ticketBody,
                createdAt: ticket.createdAt,
                status: ticket.status,
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
      this.messageService.markAsRead(msg.id, msg.type || 'defaultType').subscribe(() => msg.isRead = true);
    }
  }

  viewTicket(tck: Ticket, status: string): void {
    this.selectedTicket = tck;
    if (tck.status !== 'Pending' && tck.status !== 'Resolved') {
      tck.status = 'Pending';
      this.messageService.markAsRead(tck.id, tck.status).subscribe(() => tck.status == 'Pending');
    }
  }

  finishTicket(tck: Ticket, status: string): void {
    this.selectedTicket = tck;
    if (tck.status !== 'Resolved') {
      tck.status = 'Resolved';
      this.messageService.markAsRead(tck.id, tck.status).subscribe(() => tck.status == 'Resolved');
      this.closeTicket();
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
  deleteTicket(id: number): void {
    if (confirm('Are you sure you want to delete this ticket?')) {
      this.userService.deleteTicket(id).subscribe(() => {
        this.ticket = this.ticket.filter(t => t.id !== id);
        this.selectedTicket = null;
      });
    }
  }

  closeMessage(): void {
    this.selectedMessage = null;
  }

  closeTicket(): void {
    this.selectedTicket = null;
    // window.location.reload();
  }

  get filteredMessages(): Message[] {
    return this.messages
      .slice() // új tömb, az eredetit nem módosítjuk
      .sort((a, b) => {
        const dateA = new Date(a.createdAt).getTime();
        const dateB = new Date(b.createdAt).getTime();
        return dateB - dateA; // legfrissebb előre
      });
  }


  get filteredTickets(): Ticket[] {
    return this.ticket
      .filter(t => {
        const statusMatch =
          this.ticketFilterStatus === 'all' || t.status.toLowerCase() === this.ticketFilterStatus.toLowerCase();
        const userMatch = !this.showOnlyOwnTickets || t.userId === this.currentUserId;
        return statusMatch && userMatch;
      })
      .sort((a, b) => {
        const dateA = new Date(a.createdAt).getTime();
        const dateB = new Date(b.createdAt).getTime();
        return dateB - dateA; // legfrissebb előre
      });
  }



  createTicket(): void {
    const body = this.newTicketText.trim();
    const userId = this.currentUserId;

    if (!body) {
      alert('Ticket body cannot be empty!');
      return;
    }
    this.userService.createTicket(userId, body).subscribe({
      next: (res) => {
        const createdTicket = res.result || res;
        this.ticket.unshift({
          id: createdTicket.ticketId,
          name: '',
          email: '',
          subject: `'`,
          message: body,
          createdAt: '',
          status: 'Open',
          type: 'ticket',
          userId: userId
        });

        this.newTicketText = '';
        this.isCreatingTicket = false;
        alert('✔ Ticket created successfully!');
      },
      error: err => {
        console.error('❌ Ticket creation error:', err);
        alert('Failed to create ticket!');
      }
    });
  }

  submitReply(): void {
    if (!this.replyText.trim()) {
      alert('Reply text cannot be empty!');
      return;
    }
    if (!this.selectedMessage) {
      alert('No message selected for reply!');
      return;
    }

    this.userService.getUserById(this.currentUserId).subscribe({
      next: user => {
        const replyBody = {
          to: this.selectedMessage?.email,
          originalMessage: this.selectedMessage?.message || '',
          replyBody: this.replyText,
          adminName: user.result.username,
          ticketLink: ''
        };

        this.messageService.sendReMessage(replyBody).subscribe({
          next: (res) => {
            console.log('✅ Reply sent:', res);
            alert('Reply sent successfully!');
            this.replyText = '';
            this.isReplying = false;
          },
          error: (err) => {
            console.error('❌ Reply sending error:', err);
            alert('Failed to send reply!');
          }
        });
      },
      error: err => {
        console.error('❌ Failed to fetch user for reply:', err);
        alert('Could not fetch admin username.');
      }
    });
  }


}
