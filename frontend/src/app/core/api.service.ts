import { Injectable, inject } from '@angular/core';

import {
  HttpClient,
  HttpHeaders,
  HttpParams
} from '@angular/common/http';

import { Observable, switchMap } from 'rxjs';

import { CsrfToken } from '../models/api.models';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private readonly http = inject(HttpClient);

  get<T>(
    path: string,
    params?: HttpParams
  ): Observable<T> {

    return this.http.get<T>(`/api${path}`, {
      params,
      withCredentials: true
    });
  }

  post<T>(
    path: string,
    body: unknown
  ): Observable<T> {

    return this.mutate<T>('POST', path, body);
  }

  put<T>(
    path: string,
    body: unknown
  ): Observable<T> {

    return this.mutate<T>('PUT', path, body);
  }

  delete<T>(
    path: string
  ): Observable<T> {

    return this.mutate<T>('DELETE', path, null);
  }

  private mutate<T>(
    method: string,
    path: string,
    body: unknown
  ): Observable<T> {

    return this.get<CsrfToken>('/auth/csrf').pipe(

      switchMap(csrf => {

        const headers = new HttpHeaders()
          .set(csrf.headerName, csrf.token);

        return this.http.request<T>(
          method,
          `/api${path}`,
          {
            body,
            headers,
            withCredentials: true
          }
        );
      })
    );
  }
}