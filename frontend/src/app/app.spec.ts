import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import {
  provideHttpClient
} from '@angular/common/http';

import {
  provideHttpClientTesting,
  HttpTestingController
} from '@angular/common/http/testing';

import { App } from './app';

describe('App', () => {

  let httpTesting: HttpTestingController;

  beforeEach(async () => {

    await TestBed.configureTestingModule({

      imports: [App],

      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting()
      ]

    }).compileComponents();

    httpTesting = TestBed.inject(
      HttpTestingController
    );
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should create the application', () => {

    const fixture =
      TestBed.createComponent(App);

    expect(fixture.componentInstance)
      .toBeTruthy();
  });

  it('should display the public navigation', () => {

    const fixture =
      TestBed.createComponent(App);

    fixture.detectChanges();

    const request = httpTesting.expectOne(
      '/api/auth/me'
    );

    request.flush(null, {
      status: 401,
      statusText: 'Unauthorized'
    });

    fixture.detectChanges();

    const element =
      fixture.nativeElement as HTMLElement;

    expect(element.textContent)
      .toContain('CAR-HMS');

    expect(element.textContent)
      .toContain('Randonnées');

    expect(element.textContent)
      .toContain('Connexion');

    expect(element.textContent)
      .toContain('Inscription');

    expect(element.textContent)
      .not.toContain('Administration');
  });

  it('should display organizer navigation', () => {

    const fixture =
      TestBed.createComponent(App);

    fixture.detectChanges();

    const request = httpTesting.expectOne(
      '/api/auth/me'
    );

    request.flush({

      email: 'organizer@carhms.ca',

      roles: ['ROLE_HIKER'],

      organizer: true
    });

    fixture.detectChanges();

    const element =
      fixture.nativeElement as HTMLElement;

    expect(element.textContent)
      .toContain('Mon profil');

    expect(element.textContent)
      .toContain('Créer une randonnée');

    expect(element.textContent)
      .toContain('Déconnexion');

    expect(element.textContent)
      .not.toContain('Administration');
  });

  it('should display administrator navigation', () => {

    const fixture =
      TestBed.createComponent(App);

    fixture.detectChanges();

    const request = httpTesting.expectOne(
      '/api/auth/me'
    );

    request.flush({

      email: 'admin@carhms.ca',

      roles: ['ROLE_ADMINISTRATOR'],

      organizer: false
    });

    fixture.detectChanges();

    const element =
      fixture.nativeElement as HTMLElement;

    expect(element.textContent)
      .toContain('Administration');

    expect(element.textContent)
      .toContain('Déconnexion');

    expect(element.textContent)
      .not.toContain('Créer une randonnée');

    expect(element.textContent)
      .not.toContain('Mon profil');
  });
});