import { Component, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NgApexchartsModule, ApexChart, ApexXAxis, ApexOptions, ApexDataLabels, ApexTitleSubtitle, ApexAxisChartSeries } from 'ng-apexcharts';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import { Title } from '@angular/platform-browser';
import { BlogService } from '../../services/blog.service';
import { CartService } from '../../services/cart.service';


@Component({
  selector: 'app-admin-home',
  imports: [CommonModule, RouterModule, NgApexchartsModule],
  templateUrl: './admin-home.component.html',
  styleUrl: './admin-home.component.css'
})

export class AdminHomeComponent implements OnInit {
  userCount = 0;
  activeCount = 0;
  deletedCount = 0;
  adminCount = 0;
  monthlySignupCount = 0;
  productCount = 0;
  totalStock = 0;
  ticketCount = 0;
  postsCount = 0;
  ordersCount = 0;



  constructor(private userService: UserService, private productService: ProductService, private titleService: Title, private blogService: BlogService, private cartService: CartService) { 
    titleService.setTitle("Admin | home");
  }

  ngOnInit(): void {
    this.userService.getAllUsers().subscribe({
      next: (res) => {
        const users = res.result || res;
        this.userCount = users.length;
        this.activeCount = users.filter((u: any) => !u.isDeleted).length;
        this.deletedCount = users.filter((u: any) => u.isDeleted).length;
        this.adminCount = users.filter((u: any) => u.isAdmin).length;
        const monthlyCounts = this.getUserRegistrationsPerMonth(users);
        const currentMonthIndex = new Date().getMonth();
        this.monthlySignupCount = monthlyCounts[currentMonthIndex];

        this.chartOptionss = {
          ...this.chartOptionss,
          series: [{ name: 'Users', data: monthlyCounts }]
        };
      },
      error: err => console.error(err)
    });
    this.cartService.getAllOrders().subscribe({
      next: (res) => {
        const orders = res.result || res;
        this.ordersCount = orders.length;
        console.log('Orders:', orders);
      },
      error: err => console.error('❌ Rendelések betöltése sikertelen', err)
    });
    this.productService.getProducts().subscribe({
      next: (res) => {
        const products: Product[] = res || res;
        this.productCount = products.length;
        this.totalStock = products.reduce((sum, p) => sum + (p.stockQuanty || 0), 0);
      },
      error: err => console.error('❌ Termékek betöltése sikertelen', err)
    });
    this.userService.getAllTicket().subscribe({
      next: (tickets) => {
        const ticketData = tickets.result || tickets.result;
        this.ticketCount = tickets.result.length;
        console.log('Tickets:', tickets);
      },
      error: (error) => {
        console.error('Ticket hiba:', error);
      }
    });
    this.blogService.getAllPosts().subscribe({
      next: (post) => {
        const postData = post || post;
        this.postsCount = post.length;
        console.log('posts: ', post);
      }
    })
  }

  get statCards() {
    return [
      { label: 'Total Users', value: this.userCount, icon: '👥', bg: 'bg-primary text-light' },
      { label: 'Active Users', value: this.activeCount, icon: '✅', bg: 'bg-success text-light' },
      { label: 'Deleted Users', value: this.deletedCount, icon: '❌', bg: 'bg-danger text-light' },
      { label: 'Admins', value: this.adminCount, icon: '🛡️', bg: 'bg-dark text-light' },
      { label: 'Monthly Signups', value: this.monthlySignupCount, icon: '📈', bg: 'bg-info text-light' },

      { label: 'Products in Store', value: this.productCount, icon: '📦', bg: 'bg-secondary text-light' },
      { label: 'Total Stock', value: this.totalStock, icon: '🏷️', bg: 'bg-warning text-dark' },
      { label: 'Pending Orders', value: this.ordersCount, icon: '🛒', bg: 'bg-warning text-light' },

      { label: 'Support Tickets', value: this.ticketCount, icon: '🎫', bg: 'bg-light text-dark' },
      { label: 'Blog posts', value: this.postsCount, icon: '✏️', bg: 'bg-success text-dark' }
    ];
  }


  public chartOptionss: Partial<ApexOptions> = {

    series: [
      {
        name: "Users",
        data: new Array(12).fill(0)
      }
    ],
    chart: {
      type: "line",
      height: 350
    },
    title: {
      text: "User registrations per month"
    },
    xaxis: {
      categories: [
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
      ]
    },
    dataLabels: {
      enabled: true
    }
  };

  getUserRegistrationsPerMonth(users: any[]): number[] {
    const counts = new Array(12).fill(0);
    console.log('--- Feldolgozás indul ---');

    users.forEach((user, i) => {
      const raw = user.createdAt;
      const cleaned = raw
        .replace('CET', 'GMT')
        .replace('CEST', 'GMT');

      const parsed = Date.parse(cleaned);
      if (!isNaN(parsed)) {
        const date = new Date(parsed);
        const month = date.getMonth();
        counts[month]++;
        console.log(`✔️ OK: ${raw} => ${month}`);
      } else {
        console.warn(`❌ Nem sikerült parse-olni: ${raw}`);
      }
    });

    console.log('📊 Havi bontás:', counts);
    return counts;
  }




}
