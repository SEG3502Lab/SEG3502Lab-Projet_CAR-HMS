import {
  Component,
  inject,
  signal
} from '@angular/core';

import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { HikerService } from '../../core/hiker.service';

import {
  RegisterHikerRequest
} from '../../models/api.models';

@Component({
  selector: 'app-register-hiker',
  imports: [
    FormsModule,
    RouterLink
  ],
  templateUrl: './register-hiker.html',
  styleUrl: './register-hiker.css'
})
export class RegisterHiker {

  private readonly hikerService =
    inject(HikerService);

  readonly loading = signal(false);
  readonly errorMessage = signal('');
  readonly success = signal(false);

  readonly skillLevels = [
    'débutant',
    'intermédiaire',
    'avancé',
    'expert'
  ];

  confirmPassword = '';

  form: RegisterHikerRequest = {

    invitationCode: '',
    email: '',
    password: '',

    firstName: '',
    lastName: '',
    phoneNumber: '',

    streetAddress: '',
    city: '',
    province: '',
    postalCode: '',

    dateOfBirth: '',

    emergencyContactName: '',
    emergencyContactPhone: '',

    skillLevel: 'débutant',

    waiverAccepted: false,
    guardianName: null
  };

  isMinor(): boolean {

    if (!this.form.dateOfBirth) {
      return false;
    }

    const birth = new Date(
      this.form.dateOfBirth + 'T00:00:00'
    );

    const today = new Date();

    let age =
      today.getFullYear() - birth.getFullYear();

    const monthDifference =
      today.getMonth() - birth.getMonth();

    if (
      monthDifference < 0 ||
      (
        monthDifference === 0 &&
        today.getDate() < birth.getDate()
      )
    ) {
      age--;
    }

    return age < 18;
  }

  register(): void {

    if (this.loading()) {
      return;
    }

    this.errorMessage.set('');

    if (
      this.form.password !== this.confirmPassword
    ) {

      this.errorMessage.set(
        'Les mots de passe ne correspondent pas.'
      );

      return;
    }

    if (!this.form.waiverAccepted) {

      this.errorMessage.set(
        'Vous devez accepter la décharge de responsabilité.'
      );

      return;
    }

    if (
      this.isMinor() &&
      !this.form.guardianName?.trim()
    ) {

      this.errorMessage.set(
        'Le nom du parent ou du tuteur est obligatoire.'
      );

      return;
    }

    const request: RegisterHikerRequest = {

      ...this.form,

      invitationCode:
        this.form.invitationCode.trim(),

      email:
        this.form.email.trim(),

      guardianName: this.isMinor()
        ? this.form.guardianName?.trim() || null
        : null
    };

    this.loading.set(true);

    this.hikerService.register(request).pipe(

      finalize(() => this.loading.set(false))

    ).subscribe({

      next: () => {

        this.form.password = '';
        this.confirmPassword = '';

        this.success.set(true);
      },

      error: error => {

        this.errorMessage.set(

          error.error?.message ||

          'Impossible de créer le compte. Vérifiez les informations saisies.'
        );
      }
    });
  }
}