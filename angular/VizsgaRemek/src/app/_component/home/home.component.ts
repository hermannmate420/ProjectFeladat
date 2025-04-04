import { Component } from '@angular/core';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { FooterComponent } from '../footer/footer.component';
import { FormsModule } from '@angular/forms';
import { HomeBodyComponent } from '../home-body/home-body.component';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-home',
  imports: [NavBarComponent, FooterComponent, FormsModule, HomeBodyComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  constructor(private titleService: Title) {
    titleService.setTitle("Home");
  }
}
