import {
  Component,
  inject,
  signal
} from '@angular/core';

import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';

import { AuthService } from '../../core/auth.service';
import { HikerService } from '../../core/hiker.service';

import {
  HikerProfile,
  UpdateMembershipRequest,
  RecordCertificationRequest
} from '../../models/api.models';

@Component({
  selector: 'app-admin',
  imports: [FormsModule],
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class Admin {

  private readonly auth = inject(AuthService);

  private readonly hikerService =
    inject(HikerService);

  readonly profile = signal<HikerProfile | null>(null);

  readonly loading = signal(false);
  readonly saving = signal(false);

  readonly errorMessage = signal('');
  readonly successMessage = signal('');

  searchEmail = '';

  newStatus = 'active';

  certificateNumber = '';
  issueDate = '';
  expiryDate = '';
  organizerLevel = 'facile';

  readonly statuses = [
    { value: 'active', label: 'Active' },
    { value: 'expired', label: 'Expirée' },
    { value: 'suspended', label: 'Suspendue' }
  ];

  readonly difficulties = [
    'facile',
    'modéré',
    'difficile',
    'très difficile'
  ];

  search(): void {

    if (this.loading() || this.saving()) {
      return;
    }

    this.errorMessage.set('');
    this.successMessage.set('');
    this.profile.set(null);

    this.loadProfile(this.searchEmail.trim());
  }

  private loadProfile(email: string): void {

    this.loading.set(true);

    this.hikerService.getHikerProfile(email).pipe(

      finalize(() => this.loading.set(false))

    ).subscribe({

      next: profile => {

        this.profile.set(profile);

        this.newStatus = profile.membershipStatus;

        this.certificateNumber = '';
        this.issueDate = '';
        this.expiryDate = '';

        this.organizerLevel =
          profile.organizerLevel || 'facile';
      },

      error: error => {

        this.errorMessage.set(

          error.error?.message ||

          'Impossible de trouver ce randonneur.'
        );
      }
    });
  }

  updateMembership(): void {

    const profile = this.profile();
    const admin = this.auth.user();

    if (
      !profile ||
      !admin ||
      this.saving()
    ) {
      return;
    }

    this.errorMessage.set('');
    this.successMessage.set('');

    const request: UpdateMembershipRequest = {

      adminEmail: admin.email,

      hikerEmail: profile.email,

      newStatus: this.newStatus
    };

    this.saving.set(true);

    this.hikerService.updateMembership(request).pipe(

      finalize(() => this.saving.set(false))

    ).subscribe({

      next: () => {

        this.successMessage.set(
          'Le statut de l’adhésion a été mis à jour.'
        );

        this.loadProfile(profile.email);
      },

      error: error => {

        this.errorMessage.set(

          error.error?.message ||

          'Impossible de modifier l’adhésion.'
        );
      }
    });
  }

  recordCertification(): void {

    const profile = this.profile();
    const admin = this.auth.user();

    if (
      !profile ||
      !admin ||
      this.saving()
    ) {
      return;
    }

    this.errorMessage.set('');
    this.successMessage.set('');

    if (this.expiryDate <= this.issueDate) {

      this.errorMessage.set(
        'La date d’expiration doit être postérieure à la date de délivrance.'
      );

      return;
    }

    const request: RecordCertificationRequest = {

      adminEmail: admin.email,

      hikerEmail: profile.email,

      certificateNumber:
        this.certificateNumber.trim(),

      issueDate: this.issueDate,

      expiryDate: this.expiryDate,

      organizerLevel: this.organizerLevel
    };

    this.saving.set(true);

    this.hikerService.recordCertification(request).pipe(

      finalize(() => this.saving.set(false))

    ).subscribe({

      next: () => {

        this.successMessage.set(
          'La certification a été enregistrée avec succès.'
        );

        this.loadProfile(profile.email);
      },

      error: error => {

        this.errorMessage.set(

          error.error?.message ||

          'Impossible d’enregistrer la certification.'
        );
      }
    });
  }
}