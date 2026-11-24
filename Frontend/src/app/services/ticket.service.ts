import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TicketService {

  private apiUrl = '/api/tickets';
  private appUrl = '/api/applications';

  constructor(private http: HttpClient) {}

  getAll(): Observable<any> {
    return this.http.get(this.apiUrl);
  }

  getAllPagine(page: number, size: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/pagine?page=${page}&size=${size}`);
  }

  getById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }

  create(tache: any): Observable<any> {
    return this.http.post(this.apiUrl, tache);
  }

  changerStatut(id: number, statut: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/statut/${statut}`, {});
  }

  getApplications(): Observable<any> {
    return this.http.get(this.appUrl);
  }

  getStatsStatut(): Observable<any> {              // ← ajouté
    return this.http.get(`${this.apiUrl}/stats-statut`);
  }

  renommerApplication(id: number, nouveauNom: string): Observable<any> {
  return this.http.put(`${this.appUrl}/${id}/nom`,
    { nomApplication: nouveauNom },
    { responseType: 'text' }
  );
}
}