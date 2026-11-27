import { Component, inject, OnInit } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { AuthService } from './core/auth.service';

@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {

  readonly auth = inject(AuthService);

  private readonly router = inject(Router);

  errorMessage = '';

  ngOnInit(): void {
    this.auth.restoreSession().subscribe();
  }

  signOut(): void {

    this.errorMessage = '';

    this.auth.signOut().subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: () => {
        this.errorMessage =
          'Impossible de se déconnecter.';
      }
    });
  }
}