import { Injectable } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { LoginService } from '../services/login.service';

export const authGuard: CanActivateFn = () => {
  const loginService = inject(LoginService);
  const router = inject(Router);

  const token = localStorage.getItem('token');
  const isLoggedIn = !!token;

  if (!isLoggedIn) {
    router.navigate(['/login']);
    return false;
  }

  return true;
};