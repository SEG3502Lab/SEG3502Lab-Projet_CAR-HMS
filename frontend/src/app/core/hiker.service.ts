import { Injectable, inject } from '@angular/core';
import { HttpParams } from '@angular/common/http';

import { ApiService } from './api.service';

import {
  RegisterHikerRequest,
  HikerProfile,
  UpdateHikerRequest,
  UpdateMembershipRequest,
  RecordCertificationRequest
} from '../models/api.models';

@Injectable({
  providedIn: 'root'
})
export class HikerService {

  private readonly api = inject(ApiService);

  register(request: RegisterHikerRequest) {

    return this.api.post<{ hikerId: string }>(
      '/hikers/register',
      request
    );
  }

  getMyProfile() {

    return this.api.get<HikerProfile>(
      '/hikers/me'
    );
  }

  updateProfile(request: UpdateHikerRequest) {

    return this.api.put<void>(
      '/hikers/me',
      request
    );
  }

  getHikerProfile(email: string) {

    const params = new HttpParams()
      .set('email', email);

    return this.api.get<HikerProfile>(
      '/admin/hikers/profile',
      params
    );
  }

  updateMembership(
    request: UpdateMembershipRequest
  ) {

    return this.api.put<void>(
      '/admin/hikers/membership',
      request
    );
  }

  recordCertification(
    request: RecordCertificationRequest
  ) {

    return this.api.put<void>(
      '/admin/hikers/certification',
      request
    );
  }
}