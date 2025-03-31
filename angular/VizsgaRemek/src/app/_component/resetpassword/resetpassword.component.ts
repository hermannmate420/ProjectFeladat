import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FooterComponent } from '../footer/footer.component';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-resetpassword',
  imports: [FooterComponent, FormsModule],
  templateUrl: './resetpassword.component.html',
  styleUrl: './resetpassword.component.css'
})
export class ResetpasswordComponent {
  constructor(private titleService: Title) {
    titleService.setTitle("Forgot Password");
  }

}
