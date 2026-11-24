import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TacheService } from 'src/app/services/tache.service';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-tache',
  templateUrl: './tache.component.html',
  styleUrls: ['./tache.component.css']
})
export class TacheComponent implements OnInit {

  taches: any[] = [];
  chargement: boolean = false;
  erreur: string = '';
  succes: string = '';
  user: any;

  constructor(
    private tacheService: TacheService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.user = this.authService.getUser();
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }
    this.chargerTaches();
  }

  chargerTaches(): void {
    this.chargement = true;
    this.tacheService.getAll().subscribe({
      next: (data) => {
        this.taches = data;
        this.chargement = false;
      },
      error: (err: any) => {
        this.erreur = 'Erreur lors du chargement des tâches';
        this.chargement = false;
      }
    });
  }

  terminerTache(id: number): void {
    this.tacheService.terminer(id).subscribe({
      next: (data) => {
        const index = this.taches.findIndex(t => t.idTache === id);
        if (index !== -1) {
          this.taches[index] = data;
        }
        this.succes = 'Tâche marquée comme terminée';
        setTimeout(() => this.succes = '', 3000);
      },
      error: (err: any) => {
        this.erreur = 'Erreur lors de la mise à jour de la tâche';
        setTimeout(() => this.erreur = '', 3000);
      }
    });
  }

  retour(): void {
    this.router.navigate(['/dashboard']);
  }
}