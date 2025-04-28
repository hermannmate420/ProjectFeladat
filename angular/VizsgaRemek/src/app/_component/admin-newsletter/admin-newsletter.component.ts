import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { EmailService } from '../../services/email.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NewsletterSubscriber } from '../../models/newsletter-subscriber.model';

@Component({
  selector: 'app-admin-newsletter',
  imports: [FormsModule, CommonModule],
  templateUrl: './admin-newsletter.component.html',
  styleUrl: './admin-newsletter.component.css'
})
export class AdminNewsletterComponent implements OnInit {
  subscribers: NewsletterSubscriber[] = [];
  paginatedSubscribers: NewsletterSubscriber[] = [];
  error = '';
  currentPage = 1;
  itemsPerPage = 10;
  newsletterMessage = '';
  selectAll = false;
  previewSubject = '';
  newsletterSubject = '';
  newsletterMainContent = '';
  newsletterContent = '';
  newsletterOtherContent = '';
  newsletterLink = '';
  newsletterYear = new Date().getFullYear();


  constructor(private titleService: Title, private emailService: EmailService) {
    titleService.setTitle("Admin | Newsletter");
  }
  ngOnInit(): void {
    this.loadSubscribers();
  }
  
  loadSubscribers(): void {
    this.emailService.getAllSubscribers().subscribe({
      next: (res) => {
        this.subscribers = res;
        this.updatePaginatedSubscribers();
        console.log('res: ', this.subscribers);
        
      },
      error: (err) => {
        this.error = '❌ Hiba a feliratkozók betöltésekor.';
        console.error(err);
      }
    });
  }

  updatePaginatedSubscribers(): void {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    this.paginatedSubscribers = this.subscribers.slice(start, end);
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.updatePaginatedSubscribers();
  }

  get totalPages(): number {
    return Math.ceil(this.subscribers.length / this.itemsPerPage);
  }

  toggleSelectAll(): void {
    this.paginatedSubscribers.forEach(sub => sub.selected = this.selectAll);
  }

  getSelectedEmails(): string[] {
    return this.subscribers
      .filter(sub => sub.selected)
      .map(sub => sub.email);
  }


  sendNewsletter(): void {
    const recipients = this.getSelectedEmails();
    if (!recipients.length) {
      alert('❗ Nincs kiválasztott feliratkozó!');
      return;
    }

    recipients.forEach((email: any) => {
      const payload = {
        to: email,
        subject: this.previewSubject,
        template: 'newsletter',
        name: 'Dear reader',
        mainContent: this.newsletterMainContent,
        content: this.newsletterContent,
        otherContent: this.newsletterOtherContent,
        homelink: this.newsletterLink,
        currentYear: this.newsletterYear.toString()
      };

      this.emailService.sendNewsletterEmail(payload).subscribe({
        next: () => console.log(`✅ Sent to ${email}`),
        error: err => console.error(`❌ Failed to send to ${email}`, err)
      });
    });
  }


  openPreview(): void {
    const modal = new (window as any).bootstrap.Modal(document.getElementById('previewModal'));
    modal.show();
  }

  closePreview(): void {
    const modalEl = document.getElementById('previewModal');
    const modalInstance = (window as any).bootstrap.Modal.getInstance(modalEl);
    modalInstance?.hide();
  }

  formatNewsletterMessage(message: string): string {
    return message.replace(/\n/g, '<br>');
  }

  formatContent(content: string): string {
    return content.replace(/\n/g, '<br>');
  }

  insertPromoTemplate(): void {
    this.newsletterContent = `<img src="" alt="Product Image" style="max-width:50%; border-radius:8px;">
      <p><strong>Product:</strong> </p>
      <p><strong>Original price:</strong>  Ft</p>
      <p><strong>Discount price:</strong> <span style="color:red; font-weight: bold;"> Ft</span></p>
      <a href="${this.newsletterLink || 'http://localhost:4200/shop/all'}" 
        style="display:inline-block; margin-top:12px; background:#28a745; color:#fff; padding:10px 16px; text-decoration:none; border-radius:5px;">
        🛒 Shop now
      </a>`;
  }

  insertSimpleText(): void {
    this.newsletterContent = `<p>Szia!</p>
      <p>Köszönjük, hogy feliratkoztál! Maradj velünk a legújabb hírekért, ajánlatokért és különleges akciókért!</p>
      <p>Üdvözlettel,<br><strong>Retro Vintage Shop</strong></p>`;
  }

  insertImageOnly(): void {
    this.newsletterContent = `<img src="https://via.placeholder.com/600x300?text=Hírlevél+Banner" alt="Hírlevél kép" style="max-width:100%; border-radius:8px;">`;
  }

}
