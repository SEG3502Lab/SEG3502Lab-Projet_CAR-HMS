import { Injectable, inject } from '@angular/core';
import { HttpParams } from '@angular/common/http';

import { ApiService } from './api.service';

import {
  HikeSummary,
  CreateHikeRequest,
  Trail
} from '../models/api.models';

@Injectable({
  providedIn: 'root'
})
export class HikeService {

  private readonly api = inject(ApiService);

  browse(
    trailName = '',
    date = '',
    difficulty = ''
  ) {

    let params = new HttpParams();

    if (trailName) {
      params = params.set('trailName', trailName);
    }

    if (date) {
      params = params.set('date', date);
    }

    if (difficulty) {
      params = params.set('difficulty', difficulty);
    }

    return this.api.get<HikeSummary[]>(
      '/hikes',
      params
    );
  }

  matching() {
    return this.api.get<HikeSummary[]>(
      '/hikes/matching'
    );
  }

  listTrails() {
    return this.api.get<Trail[]>('/trails');
  }

  create(request: CreateHikeRequest) {

    return this.api.post<{ hikeId: string }>(
      '/hikes',
      request
    );
  }
}