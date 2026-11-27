import {
  Component,
  inject,
  signal
} from '@angular/core';

import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-sign-in',
  imports: [
    FormsModule,
    RouterLink
  ],
  templateUrl: './sign-in.html',
  styleUrl: './sign-in.css'
})
export class SignIn {

  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  email = '';
  password = '';

  readonly loading = signal(false);
  readonly errorMessage = signal('');

  signIn(): void {

    if (this.loading()) {
      return;
    }

    this.loading.set(true);
    this.errorMessage.set('');

    this.auth.signIn(
      this.email.trim(),
      this.password
    ).pipe(

      finalize(() => this.loading.set(false))

    ).subscribe({

      next: () => {
        this.password = '';
        this.router.navigate(['/']);
      },

      error: error => {

        if (error.status === 401) {
          this.errorMessage.set(
            'Courriel ou mot de passe incorrect.'
          );
        } else {
          this.errorMessage.set(
            'Impossible de se connecter. Veuillez réessayer.'
          );
        }
      }
    });
  }
}