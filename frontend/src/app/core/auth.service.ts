import {
  Injectable,
  inject,
  signal,
  computed
} from '@angular/core';

import { HttpParams } from '@angular/common/http';

import {
  Observable,
  catchError,
  of,
  switchMap,
  tap
} from 'rxjs';

import { ApiService } from './api.service';

import {
  AuthenticatedUser
} from '../models/api.models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly api = inject(ApiService);

  private readonly currentUser =
    signal<AuthenticatedUser | null>(null);

  readonly user = this.currentUser.asReadonly();

  readonly isAuthenticated = computed(
    () => this.currentUser() !== null
  );

  readonly isAdministrator = computed(
    () => this.currentUser()
      ?.roles.includes('ROLE_ADMINISTRATOR') ?? false
  );

  readonly isOrganizer = computed(
    () => this.currentUser()?.organizer ?? false
  );

  signIn(
    email: string,
    password: string
  ): Observable<AuthenticatedUser> {

    const body = new HttpParams()
      .set('email', email)
      .set('password', password);

    return this.api.post<void>(
      '/auth/login',
      body
    ).pipe(

      switchMap(() =>
        this.api.get<AuthenticatedUser>('/auth/me')
      ),

      tap(user => this.currentUser.set(user))
    );
  }

  signOut(): Observable<void> {

    return this.api.post<void>(
      '/auth/logout',
      null
    ).pipe(

      tap(() => this.currentUser.set(null))
    );
  }

  restoreSession():
    Observable<AuthenticatedUser | null> {

    return this.api
      .get<AuthenticatedUser>('/auth/me')
      .pipe(

        tap(user => this.currentUser.set(user)),

        catchError(() => {

          this.currentUser.set(null);

          return of(null);
        })
      );
  }
}