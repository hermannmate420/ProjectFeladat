import { Routes } from '@angular/router';
import { HomeComponent } from './_component/home/home.component';
import { ProfileComponent } from './_component/profile/profile.component';
import { LoginComponent } from './_component/login/login.component';
import { RegisterComponent } from './_component/register/register.component';
import { ResetpasswordComponent } from './_component/resetpassword/resetpassword.component';
import { BlogComponent } from './_component/blog/blog.component';
import { UserProfileComponent } from './_component/user-profile/user-profile.component';
import { MypostsComponent } from './_component/myposts/myposts.component';
import { CartComponent } from './_component/cart/cart.component';
import { AdminGuard } from './guards/admin.guard';
import { AdminUserTableComponent } from './_component/admin-user-table/admin-user-table.component';
import { AdminHomeComponent } from './_component/admin-home/admin-home.component';
import { AdminProductTableComponent } from './_component/admin-product-table/admin-product-table.component';
import { AdminProductDiscountComponent } from './_component/admin-product-discount/admin-product-discount.component';
import { AdminProductCategoriesComponent } from './_component/admin-product-categories/admin-product-categories.component';
import { AdminOrdersComponent } from './_component/admin-orders/admin-orders.component';
import { AdminOrdersStockComponent } from './_component/admin-orders-stock/admin-orders-stock.component';
import { AdminNewsletterComponent } from './_component/admin-newsletter/admin-newsletter.component';
import { AdminAnalyticsComponent } from './_component/admin-analytics/admin-analytics.component';
import { AdminMessagesComponent } from './_component/admin-messages/admin-messages.component';
import { ShopLayoutComponent } from './_component/shop-layout/shop-layout.component';
import { ForgotResetPasswordComponent } from './_component/forgot-reset-password/forgot-reset-password.component';
import { ReactivateAccountComponent } from './_component/reactivate-account/reactivate-account.component';
import { ContactUsComponent } from './_component/contact-us/contact-us.component';
import { authGuard } from './guards/auth.guard';
import { GeneralTermsComponent } from './_component/general-terms/general-terms.component';
import { PrivacySecurityComponent } from './_component/privacy-security/privacy-security.component';
import { OrdersRefundsComponent } from './_component/orders-refunds/orders-refunds.component';
import { BlogListComponent } from './_component/blog-list/blog-list.component';
import { BlogEditorComponent } from './_component/blog-editor/blog-editor.component';
import { ShopAllComponent } from './_component/shop-all/shop-all.component';

export const routes: Routes = [
    { path: '', redirectTo: '/home', pathMatch: 'full' },
    { path: 'home', component: HomeComponent },
    { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
    { path: 'forgotpassword', component: ResetpasswordComponent },
    { path: 'reset-password', component: ForgotResetPasswordComponent },
    { path: 'blog', component: BlogComponent },
    { path: 'reactivate', component: ReactivateAccountComponent },
    { path: 'general-terms-and-conditions', component: GeneralTermsComponent },
    { path: 'privacy-and-security', component: PrivacySecurityComponent },
    { path: 'orders-refunds', component: OrdersRefundsComponent },
    { path: 'contact-us', component: ContactUsComponent },
    // { path: 'blog', component: BlogListComponent },
    { path: 'blog-editor', component: BlogEditorComponent },
    {
        path: 'shop',
        component: ShopLayoutComponent,
        children: [
            { path: 'all', component: ShopAllComponent },
            { path: '', redirectTo: 'all', pathMatch: 'full' },
        ]
    },


    { path: 'posts', component: MypostsComponent, canActivate: [authGuard] },
    { path: 'cart', component: CartComponent },
    {
        path: 'admin', component: UserProfileComponent, canActivate: [AdminGuard], children: [
            { path: 'homeAdmin', component: AdminHomeComponent },
            { path: 'users', component: AdminUserTableComponent },
            { path: 'products', component: AdminProductTableComponent },
            { path: 'discount', component: AdminProductDiscountComponent },
            { path: 'categories', component: AdminProductCategoriesComponent },
            { path: 'orders', component: AdminOrdersComponent },
            { path: 'stock', component: AdminOrdersStockComponent },
            { path: 'newsletter', component: AdminNewsletterComponent },
            { path: 'analytics', component: AdminAnalyticsComponent },
            { path: 'messages', component: AdminMessagesComponent },

            { path: '', redirectTo: 'homeAdmin', pathMatch: 'full' },

        ]
    },

];
