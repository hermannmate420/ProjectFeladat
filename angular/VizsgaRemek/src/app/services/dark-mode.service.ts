// dark-mode.service.ts
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class DarkModeService {
  private isDark = false;

  constructor() {
    const saved = localStorage.getItem('darkMode');
    if (saved === 'true') {
      this.enableDarkMode();
    }
  }

  toggleDarkMode(): void {
    this.isDark = !this.isDark;
    this.updateClass();
  }

  enableDarkMode(): void {
    this.isDark = true;
    this.updateClass();
  }

  disableDarkMode(): void {
    this.isDark = false;
    this.updateClass();
  }

  private updateClass(): void {
    const body = document.body;
    if (this.isDark) {
      body.classList.add('dark-mode');
      localStorage.setItem('darkMode', 'true');
    } else {
      body.classList.remove('dark-mode');
      localStorage.setItem('darkMode', 'false');
    }
  }

  isDarkMode(): boolean {
    return this.isDark;
  }
}
