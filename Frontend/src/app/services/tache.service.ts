import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TacheService {

  private apiUrl = '/api/taches';
  private userUrl = '/api/utilisateurs';

  constructor(private http: HttpClient) {}

  getAll(): Observable<any> {
    return this.http.get(this.apiUrl);
  }

  getById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }

  getByDeveloppeur(idDeveloppeur: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/developpeur/${idDeveloppeur}`);
  }

  getByTicket(idTicket: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/ticket/${idTicket}`);
  }

  create(tache: any): Observable<any> {
    return this.http.post(this.apiUrl, tache);
  }

  // idDeveloppeur optionnel — obligatoire pour DEV, absent pour ADMIN
  terminer(id: number, idDeveloppeur?: number): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/${id}/terminer`,
      idDeveloppeur ? { idDeveloppeur } : {},
      { responseType: 'text' }
    );
  }

  getDeveloppeurs(): Observable<any> {
    return this.http.get(`${this.userUrl}/developpeurs`);
  }

  // idDeveloppeur optionnel — obligatoire pour DEV, absent pour ADMIN
  changerStatut(id: number, statut: string, idDeveloppeur?: number): Observable<any> {
    const params = idDeveloppeur
      ? `statut=${statut}&idDeveloppeur=${idDeveloppeur}`
      : `statut=${statut}`;
    return this.http.put(
      `${this.apiUrl}/${id}/statut?${params}`,
      {},
      { responseType: 'text' }
    );
  }

  getByDeveloppeurPagine(idDeveloppeur: number, page: number, size: number): Observable<any> {
    return this.http.get<any>(
      `${this.apiUrl}/developpeur/${idDeveloppeur}/pagine?page=${page}&size=${size}`
    );
  }

  getHistoriqueTache(idTache: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${idTache}/historique`);
  }

  changerStatutAvecCommentaire(
    id: number,
    statut: string,
    commentaire: string,
    idDeveloppeur: number
  ): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}/statut-commentaire`, {
      statut,
      commentaire,
      idDeveloppeur
    });
  }

  ajouterRapport(idTache: number, rapport: string, idDeveloppeur: number): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/${idTache}/rapport`,
      { rapport, idDeveloppeur },
      { responseType: 'text' }
    );
  }

  reassigner(idTache: number, idDeveloppeur: number): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/${idTache}/reassigner`,
      { idDeveloppeur },
      { responseType: 'text' }
    );
  }
}