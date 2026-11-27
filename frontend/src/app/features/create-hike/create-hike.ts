import {
  Component,
  OnInit,
  inject,
  signal
} from '@angular/core';

import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { AuthService } from '../../core/auth.service';
import { HikeService } from '../../core/hike.service';

import {
  CreateHikeRequest,
  Trail
} from '../../models/api.models';

@Component({
  selector: 'app-create-hike',
  imports: [FormsModule, RouterLink],
  templateUrl: './create-hike.html',
  styleUrl: './create-hike.css'
})
export class CreateHike implements OnInit {

  private readonly auth = inject(AuthService);
  private readonly hikeService = inject(HikeService);

  readonly trails = signal<Trail[]>([]);

  readonly loading = signal(true);
  readonly saving = signal(false);

  readonly errorMessage = signal('');
  readonly createdHikeId = signal('');

  title = '';
  description = '';
  trailId = '';

  startDateTime = '';
  registrationDeadline = '';

  durationHours = 2;
  durationMinutes = 0;

  capacity = 10;

  ngOnInit(): void {
    this.loadTrails();
  }

  loadTrails(): void {

    this.loading.set(true);
    this.errorMessage.set('');

    this.hikeService.listTrails().pipe(
      finalize(() => this.loading.set(false))
    ).subscribe({

      next: trails => {
        this.trails.set(trails);
      },

      error: () => {
        this.errorMessage.set(
          'Impossible de charger les sentiers.'
        );
      }
    });
  }

  selectedTrail(): Trail | undefined {

    return this.trails().find(
      trail => trail.id === this.trailId
    );
  }

  create(): void {

    const user = this.auth.user();

    if (!user || !user.organizer || this.saving()) {
      return;
    }

    this.errorMessage.set('');

    if (
      !this.title.trim() ||
      !this.description.trim() ||
      !this.selectedTrail()
    ) {
      this.errorMessage.set(
        'Veuillez remplir tous les champs obligatoires.'
      );

      return;
    }

    const start = new Date(this.startDateTime);
    const deadline = new Date(this.registrationDeadline);

    if (
      Number.isNaN(start.getTime()) ||
      Number.isNaN(deadline.getTime())
    ) {
      this.errorMessage.set(
        'Veuillez saisir des dates valides.'
      );

      return;
    }

    if (start.getTime() <= Date.now()) {

      this.errorMessage.set(
        'La randonnée doit être prévue dans le futur.'
      );

      return;
    }

    if (
      deadline.getTime() >= start.getTime() ||
      deadline.getTime() <= Date.now()
    ) {

      this.errorMessage.set(
        'La date limite doit être dans le futur et précéder le début de la randonnée.'
      );

      return;
    }

    const totalMinutes =
      this.durationHours * 60 + this.durationMinutes;

    if (
      !Number.isInteger(totalMinutes) ||
      totalMinutes <= 0
    ) {

      this.errorMessage.set(
        'La durée doit être supérieure à zéro.'
      );

      return;
    }

    if (
      !Number.isInteger(this.capacity) ||
      this.capacity < 1
    ) {

      this.errorMessage.set(
        'La capacité doit être un entier positif.'
      );

      return;
    }

    const request: CreateHikeRequest = {

      organizerEmail: user.email,

      title: this.title.trim(),
      description: this.description.trim(),
      trailId: this.trailId,

      startDateTime: this.startDateTime,

      registrationDeadline:
        this.registrationDeadline,

      expectedDuration: `PT${totalMinutes}M`,

      capacity: this.capacity
    };

    this.saving.set(true);

    this.hikeService.create(request).pipe(
      finalize(() => this.saving.set(false))
    ).subscribe({

      next: response => {
        this.createdHikeId.set(response.hikeId);
      },

      error: error => {

        this.errorMessage.set(

          error.error?.message ||

          'Impossible de créer la randonnée.'
        );
      }
    });
  }

  reset(): void {

    this.title = '';
    this.description = '';
    this.trailId = '';

    this.startDateTime = '';
    this.registrationDeadline = '';

    this.durationHours = 2;
    this.durationMinutes = 0;
    this.capacity = 10;

    this.errorMessage.set('');
    this.createdHikeId.set('');
  }
}