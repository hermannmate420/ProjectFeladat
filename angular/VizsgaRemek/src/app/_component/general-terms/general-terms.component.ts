import { Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { FooterComponent } from '../footer/footer.component';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-general-terms',
  imports: [FormsModule, NavBarComponent, FooterComponent, ReactiveFormsModule, CommonModule, RouterModule],
  templateUrl: './general-terms.component.html',
  styleUrl: './general-terms.component.css'
})
export class GeneralTermsComponent {
constructor(private titleService: Title){
  titleService.setTitle("General terms and Conditions");
}
}
