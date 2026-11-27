import { Routes } from '@angular/router';

import { BrowseHikes }
  from './features/browse-hikes/browse-hikes';

import { SignIn }
  from './features/sign-in/sign-in';

import { RegisterHiker }
  from './features/register-hiker/register-hiker';

import { UpdateHiker }
  from './features/update-hiker/update-hiker';

import { Admin }
  from './features/admin/admin';

import { CreateHike }
  from './features/create-hike/create-hike';

import {
  hikerGuard,
  organizerGuard,
  administratorGuard
} from './core/auth.guard';

export const routes: Routes = [

  {
    path: '',
    pathMatch: 'full',
    component: BrowseHikes,
    title: 'Randonnées | CAR-HMS'
  },

  {
    path: 'login',
    component: SignIn,
    title: 'Connexion | CAR-HMS'
  },

  {
    path: 'register',
    component: RegisterHiker,
    title: 'Inscription | CAR-HMS'
  },

  {
    path: 'profile',
    component: UpdateHiker,
    canActivate: [hikerGuard],
    title: 'Mon profil | CAR-HMS'
  },

  {
    path: 'admin',
    component: Admin,
    canActivate: [administratorGuard],
    title: 'Administration | CAR-HMS'
  },

  {
    path: 'create-hike',
    component: CreateHike,
    canActivate: [organizerGuard],
    title: 'Créer une randonnée | CAR-HMS'
  },

  {
    path: '**',
    redirectTo: ''
  }
];