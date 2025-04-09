import { Component } from '@angular/core';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-admin-newsletter',
  imports: [],
  templateUrl: './admin-newsletter.component.html',
  styleUrl: './admin-newsletter.component.css'
})
export class AdminNewsletterComponent {
  constructor(private titleService: Title) {
    titleService.setTitle("Admin | Newsletter");
  }

}
