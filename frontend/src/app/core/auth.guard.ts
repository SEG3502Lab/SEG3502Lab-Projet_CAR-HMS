import { inject } from '@angular/core';

import {
  CanActivateFn,
  Router
} from '@angular/router';

import { map } from 'rxjs';

import { AuthService } from './auth.service';

export const hikerGuard: CanActivateFn = () => {

  const auth = inject(AuthService);
  const router = inject(Router);

  return auth.restoreSession().pipe(

    map(user => {

      if (!user) {
        return router.createUrlTree(['/login']);
      }

      if (
        user.roles.includes('ROLE_ADMINISTRATOR')
      ) {
        return router.createUrlTree(['/']);
      }

      return true;
    })
  );
};

export const organizerGuard: CanActivateFn = () => {

  const auth = inject(AuthService);
  const router = inject(Router);

  return auth.restoreSession().pipe(

    map(user => {

      if (!user) {
        return router.createUrlTree(['/login']);
      }

      if (!user.organizer) {
        return router.createUrlTree(['/']);
      }

      return true;
    })
  );
};

export const administratorGuard: CanActivateFn = () => {

  const auth = inject(AuthService);
  const router = inject(Router);

  return auth.restoreSession().pipe(

    map(user => {

      if (!user) {
        return router.createUrlTree(['/login']);
      }

      if (
        !user.roles.includes('ROLE_ADMINISTRATOR')
      ) {
        return router.createUrlTree(['/']);
      }

      return true;
    })
  );
};