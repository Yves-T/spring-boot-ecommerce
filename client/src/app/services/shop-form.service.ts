import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable, of } from 'rxjs';
import { Country } from '../common/country';
import { State } from '../common/state';

interface GetResponseCountries {
  _embedded: {
    countries: Country[];
  };
}

interface GetResponseStates {
  _embedded: {
    states: State[];
  };
}

@Injectable({
  providedIn: 'root',
})
export class ShopFormService {
  private countriesUrl = 'http://localhost:8080/api/countries';
  private statesUrl = 'http://localhost:8080/api/states';

  private http = inject(HttpClient);

  constructor() {}

  getCountries(): Observable<Country[]> {
    return this.http
      .get<GetResponseCountries>(this.countriesUrl)
      .pipe(map(response => response._embedded.countries));
  }

  getStates(countryCode: string): Observable<State[]> {
    const searchStatesUrl = `${this.statesUrl}/search/findByCountryCode?code=${countryCode}`;
    return this.http
      .get<GetResponseStates>(searchStatesUrl)
      .pipe(map(response => response._embedded.states));
  }

  getCreditCardMoths(startMonth: number): Observable<number[]> {
    let data: number[] = [];
    for (let month = startMonth; month <= 12; month++) {
      data.push(month);
    }

    return of(data);
  }

  getCreditCardYears(): Observable<number[]> {
    let data: number[] = [];
    const startYear = new Date().getFullYear();
    const endYear = startYear + 10;
    for (let year = startYear; year <= endYear; year++) {
      data.push(year);
    }

    return of(data);
  }
}
