import { TestBed } from '@angular/core/testing';

import {
  provideHttpClient
} from '@angular/common/http';

import {
  provideHttpClientTesting,
  HttpTestingController
} from '@angular/common/http/testing';

import { ApiService } from './api.service';

describe('ApiService', () => {

  let service: ApiService;

  let httpTesting: HttpTestingController;

  beforeEach(() => {

    TestBed.configureTestingModule({

      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(ApiService);

    httpTesting = TestBed.inject(
      HttpTestingController
    );
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should send GET requests', () => {

    service.get<unknown[]>('/hikes')
      .subscribe(result => {
        expect(result).toEqual([]);
      });

    const request = httpTesting.expectOne(
      '/api/hikes'
    );

    expect(request.request.method).toBe('GET');

    expect(request.request.withCredentials)
      .toBe(true);

    request.flush([]);
  });

  it('should include CSRF token in POST requests', () => {

    const body = {
      title: 'Test hike'
    };

    service.post('/hikes', body).subscribe();

    const csrfRequest = httpTesting.expectOne(
      '/api/auth/csrf'
    );

    expect(csrfRequest.request.method)
      .toBe('GET');

    csrfRequest.flush({

      headerName: 'X-CSRF-TOKEN',

      parameterName: '_csrf',

      token: 'test-token'
    });

    const request = httpTesting.expectOne(
      '/api/hikes'
    );

    expect(request.request.method)
      .toBe('POST');

    expect(request.request.body)
      .toEqual(body);

    expect(
      request.request.headers.get('X-CSRF-TOKEN')
    ).toBe('test-token');

    expect(request.request.withCredentials)
      .toBe(true);

    request.flush({
      hikeId: 'test-hike-id'
    });
  });

  it('should include CSRF token in PUT requests', () => {

    service.put('/hikers/me', {
      firstName: 'Thomas'
    }).subscribe();

    const csrfRequest = httpTesting.expectOne(
      '/api/auth/csrf'
    );

    csrfRequest.flush({

      headerName: 'X-CSRF-TOKEN',

      parameterName: '_csrf',

      token: 'update-token'
    });

    const request = httpTesting.expectOne(
      '/api/hikers/me'
    );

    expect(request.request.method)
      .toBe('PUT');

    expect(
      request.request.headers.get('X-CSRF-TOKEN')
    ).toBe('update-token');

    request.flush(null, {
      status: 204,
      statusText: 'No Content'
    });
  });

  it('should include CSRF token in DELETE requests', () => {

    service.delete('/hikes/test-id')
      .subscribe();

    const csrfRequest = httpTesting.expectOne(
      '/api/auth/csrf'
    );

    csrfRequest.flush({

      headerName: 'X-CSRF-TOKEN',

      parameterName: '_csrf',

      token: 'delete-token'
    });

    const request = httpTesting.expectOne(
      '/api/hikes/test-id'
    );

    expect(request.request.method)
      .toBe('DELETE');

    expect(
      request.request.headers.get('X-CSRF-TOKEN')
    ).toBe('delete-token');

    request.flush(null, {
      status: 204,
      statusText: 'No Content'
    });
  });
});