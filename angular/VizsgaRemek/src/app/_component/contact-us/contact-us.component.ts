import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { NavBarComponent } from "../nav-bar/nav-bar.component";
import { ContactUsBodyComponent } from "../contact-us-body/contact-us-body.component";
import { FooterComponent } from "../footer/footer.component";

@Component({
  selector: 'app-contact-us',
  imports: [FormsModule, NavBarComponent, ContactUsBodyComponent, FooterComponent],
  templateUrl: './contact-us.component.html',
  styleUrl: './contact-us.component.css'
})
export class ContactUsComponent {
  constructor(private titleService: Title){
    titleService.setTitle("Contact us");
  }
}
