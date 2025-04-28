import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { Title } from '@angular/platform-browser';
import { RouterModule, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MessageService } from '../../services/message.service';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-user-profile',
  imports: [NavBarComponent, FormsModule, RouterModule, CommonModule],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css'
})
export class UserProfileComponent implements OnInit {
  sidebarOpen = false;
  messageCount: number = 0;
  sidebarItems = [
    { label: 'Home', icon: '🏠', link: 'homeAdmin' },
    { label: 'Users', icon: '👤', link: 'users' },
    { label: 'Products', icon: '📦', link: 'products' },
    { label: 'Orders', icon: '📦', link: 'orders' },
    { label: 'Newsletter', icon: '📧', link: 'newsletter' },
    { label: 'Messages', icon: '📬', link: 'messages', key: 'messages' },
    { label: 'Analytics', icon: '📊', link: 'analytics' },
  ];


  constructor(private titleService: Title, private messageService: MessageService, private userService: UserService) {
    titleService.setTitle("Admin");
  }

  ngOnInit(): void {
    this.fetchMessagesAndTickets();
  }

  fetchMessagesAndTickets(): void {
    this.messageService.getMessage().subscribe({
      next: (res) => {
        const feedbacks = res.result || res;
        const unreadMessages = feedbacks.filter((m: any) => !m.isRead).length;

        this.userService.getAllTicket().subscribe({
          next: (tickets) => {
            const openOrPending = tickets.result.filter((t: any) =>
              t.status === 'Open' || t.status === 'Pending'
            ).length;

            this.messageCount = unreadMessages + openOrPending;
          },
          error: err => console.error('❌ Ticket fetch error:', err)
        });
      },
      error: err => console.error('❌ Message fetch error:', err)
    });
  }

  toggleSidebar() {
    this.sidebarOpen = !this.sidebarOpen;
  }
}
