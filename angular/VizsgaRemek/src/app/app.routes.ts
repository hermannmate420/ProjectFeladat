import { Routes } from '@angular/router';
import { HomeComponent } from './_component/home/home.component';
import { ProfileComponent } from './_component/profile/profile.component';
import { LoginComponent } from './_component/login/login.component';
import { RegisterComponent } from './_component/register/register.component';
import { ResetpasswordComponent } from './_component/resetpassword/resetpassword.component';
import { BlogComponent } from './_component/blog/blog.component';
import { ShopComponent } from './_component/shop/shop.component';
import path from 'path';
import { Component } from '@angular/core';
import { UserProfileComponent } from './_component/user-profile/user-profile.component';
import { MypostsComponent } from './_component/myposts/myposts.component';
import { CartComponent } from './_component/cart/cart.component';

export const routes: Routes = [
    {path:'', redirectTo:'/login', pathMatch:'full'},
    {path:'home', component:HomeComponent},
    {path:'profile', component:ProfileComponent},
    {path:'login', component:LoginComponent},
    {path:'register', component:RegisterComponent},
    {path:'resetpassword', component:ResetpasswordComponent},
    {path:'blog', component:BlogComponent},
    {path:'shop', component:ShopComponent},
    {path:'admin', component:UserProfileComponent},
    {path:'posts', component:MypostsComponent},
    {path:'cart', component:CartComponent},
    
];
