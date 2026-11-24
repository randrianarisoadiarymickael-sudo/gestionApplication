import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  matricule: string = '';
  motDePasse: string = '';
  erreur: string = '';
  chargement: boolean = false;

  constructor(private authService: AuthService, private router: Router) {}

  login(): void {
    this.erreur = '';
    this.chargement = true;

    if (!this.matricule || !this.motDePasse) {
      this.erreur = 'Veuillez remplir tous les champs';
      this.chargement = false;
      return;
    }

    this.authService.login(this.matricule, this.motDePasse).subscribe({
      next: (response: any) => {
        if (response.success) {
          this.authService.saveUser(response);
          if (response.role === 'ADMIN') {
            this.router.navigate(['/admin/dashboard']);
          } else if (response.role === 'DEV') {
            this.router.navigate(['/dev/dashboard']);
          } else {
            this.router.navigate(['/dashboard']);
          }
        } else {
          this.erreur = response.message;
        }
        this.chargement = false;
      },
      error: () => {
        this.erreur = 'Erreur de connexion au serveur';
        this.chargement = false;
      }
    });
  }
}