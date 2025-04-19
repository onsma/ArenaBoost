import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  template: `
    <div class="container text-center mt-5">
      <img src="assets/images/arenaboost-logo.png" alt="ArenaBoost Logo" class="mb-4" style="max-width: 300px;">
      <h1 class="text-gold">Bienvenue sur ArenaBoost</h1>
      <p class="lead text-white">Fueling Ambitions, Powering Champions</p>
      <button class="btn btn-primary mt-4" (click)="navigateToDashboard()">Accéder au Dashboard</button>
    </div>
  `,
  styles: [`
    .text-gold {
      color: #d4af37;
    }
    .container {
      height: 100vh;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
    }
    .btn-primary {
      background-color: #d4af37;
      border-color: #d4af37;
      color: #000000;
    }
    .btn-primary:hover {
      background-color: #aa8c2c;
      border-color: #aa8c2c;
    }
  `]
})
export class HomeComponent implements OnInit {

  constructor(private router: Router) { }

  ngOnInit(): void {
  }

  navigateToDashboard() {
    this.router.navigate(['/dashboard']);
  }
}
