import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = '/api/auth';

  constructor(private http: HttpClient) {}

  login(matricule: string, motDePasse: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, {
      matricule: matricule,
      motDePasse: motDePasse
    });
  }

  saveUser(user: any): void {
    localStorage.setItem('user', JSON.stringify(user));
  }

  getUser(): any {
    return JSON.parse(localStorage.getItem('user') || '{}');
  }

  isLoggedIn(): boolean {
    return localStorage.getItem('user') !== null;
  }

  logout(): void {
    localStorage.removeItem('user');
  }

  getRole(): string {
    const user = this.getUser();
    return user.role || '';
  }
}