import {
  Component,
  OnInit,
  inject,
  signal
} from '@angular/core';

import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';

import { HikerService } from '../../core/hiker.service';

import {
  HikerProfile,
  UpdateHikerRequest
} from '../../models/api.models';

@Component({
  selector: 'app-update-hiker',
  imports: [FormsModule],
  templateUrl: './update-hiker.html',
  styleUrl: './update-hiker.css'
})
export class UpdateHiker implements OnInit {

  private readonly hikerService =
    inject(HikerService);

  readonly profile = signal<HikerProfile | null>(null);

  readonly loading = signal(true);
  readonly saving = signal(false);

  readonly errorMessage = signal('');
  readonly successMessage = signal('');

  readonly skillLevels = [
    'débutant',
    'intermédiaire',
    'avancé',
    'expert'
  ];

  form: UpdateHikerRequest = {

    hikerEmail: '',

    firstName: '',
    lastName: '',
    phoneNumber: '',

    streetAddress: '',
    city: '',
    province: '',
    postalCode: '',

    emergencyContactName: '',
    emergencyContactPhone: '',

    skillLevel: ''
  };

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {

    this.loading.set(true);
    this.errorMessage.set('');

    this.hikerService.getMyProfile().pipe(

      finalize(() => this.loading.set(false))

    ).subscribe({

      next: profile => {

        this.profile.set(profile);

        this.fillForm(profile);
      },

      error: () => {

        this.errorMessage.set(
          'Impossible de charger votre profil.'
        );
      }
    });
  }

  private fillForm(profile: HikerProfile): void {

    this.form = {

      hikerEmail: profile.email,

      firstName: profile.firstName,
      lastName: profile.lastName,
      phoneNumber: profile.phoneNumber,

      streetAddress: profile.streetAddress,
      city: profile.city,
      province: profile.province,
      postalCode: profile.postalCode,

      emergencyContactName:
        profile.emergencyContactName,

      emergencyContactPhone:
        profile.emergencyContactPhone,

      skillLevel: profile.skillLevel
    };
  }

  save(): void {

    if (this.saving()) {
      return;
    }

    this.saving.set(true);

    this.errorMessage.set('');
    this.successMessage.set('');

    const request = { ...this.form };

    this.hikerService.updateProfile(request).pipe(

      finalize(() => this.saving.set(false))

    ).subscribe({

      next: () => {

        this.successMessage.set(
          'Votre profil a été mis à jour avec succès.'
        );

        this.loadProfile();
      },

      error: error => {

        this.errorMessage.set(

          error.error?.message ||

          'Impossible de modifier votre profil.'
        );
      }
    });
  }

  reset(): void {

    const profile = this.profile();

    if (profile) {
      this.fillForm(profile);
    }

    this.errorMessage.set('');
    this.successMessage.set('');
  }
}