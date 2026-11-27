import {
  Component,
  OnInit,
  inject,
  signal
} from '@angular/core';

import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';

import { HikeService } from '../../core/hike.service';
import { AuthService } from '../../core/auth.service';

import {
  HikeSummary
} from '../../models/api.models';

@Component({
  selector: 'app-browse-hikes',
  imports: [
    FormsModule,
    DatePipe
  ],
  templateUrl: './browse-hikes.html',
  styleUrl: './browse-hikes.css'
})
export class BrowseHikes implements OnInit {

  private readonly hikeService =
    inject(HikeService);

  readonly auth = inject(AuthService);

  readonly hikes = signal<HikeSummary[]>([]);
  readonly loading = signal(false);
  readonly errorMessage = signal('');

  trailName = '';
  date = '';
  difficulty = '';

  readonly difficulties = [
    'facile',
    'modéré',
    'difficile',
    'très difficile'
  ];

  ngOnInit(): void {
    this.search();
  }

  search(): void {

    this.loading.set(true);
    this.errorMessage.set('');

    this.hikeService.browse(
      this.trailName,
      this.date,
      this.difficulty
    ).subscribe({
      next: hikes => {
        this.hikes.set(hikes);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set(
          'Impossible de charger les randonnées.'
        );
        this.loading.set(false);
      }
    });
  }

  resetFilters(): void {

    this.trailName = '';
    this.date = '';
    this.difficulty = '';

    this.search();
  }

  showMatching(): void {

    this.loading.set(true);
    this.errorMessage.set('');

    this.hikeService.matching().subscribe({
      next: hikes => {
        this.hikes.set(hikes);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set(
          'Impossible de charger les randonnées adaptées.'
        );
        this.loading.set(false);
      }
    });
  }
}