import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HistoriqueService {

  private apiUrl = '/api/historiques';

  constructor(private http: HttpClient) {}

  getAll(): Observable<any> {
    return this.http.get(this.apiUrl);
  }

  getByTicket(idTicket: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/ticket/${idTicket}`);
  }
}