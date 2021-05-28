import { Injectable } from '@angular/core';

import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable()
export class HttpClientService {

  private httpOptions: any = {

    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    }),
    body: null
  };

  private host: string = 'http://localhost:5555/app';

  constructor(private http: HttpClient) {
    this.setAuthorization('my-auth-token');
  }

  public get(): Promise<any[]> {
    return this.http.get(this.host + '/get', this.httpOptions)
    .toPromise()
    .then((res) => {
      const response: any = res;
      return response;
    })
    .catch(this.errorHandler);
  }

  private errorHandler(err) {
    console.log('Error occured.', err);
    return Promise.reject(err.message || err);
  }

  public setAuthorization(token: string = null): void {
    if (!token) {
      return;
    }
    const bearerToken: string = `Bearer ${token}`;
    this.httpOptions.headers = this.httpOptions.headers.set('Authorization', bearerToken);
  }
}
