import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FooterComponent } from '../footer/footer.component';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-privacy-security',
  imports: [FormsModule, CommonModule, FooterComponent, NavBarComponent, RouterModule],
  templateUrl: './privacy-security.component.html',
  styleUrl: './privacy-security.component.css'
})
export class PrivacySecurityComponent {

}
