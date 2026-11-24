import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TicketService } from 'src/app/services/ticket.service';
import { TacheService } from 'src/app/services/tache.service';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-detail-ticket',
  templateUrl: './detail-ticket.component.html',
  styleUrls: ['./detail-ticket.component.css']
})
export class DetailTicketComponent implements OnInit {

  ticket: any = null;
  taches: any[] = [];
  developpeurs: any[] = [];
  chargement: boolean = false;
  erreur: string = '';
  succes: string = '';
  user: any;
  afficherFormTache: boolean = false;

  nouvelleTache = {
    titre: '',
    description: '',
    priorite: 'MOYENNE',
    statut: 'EN_ATTENTE',
    ticket: { idTicket: null as any },
    developpeur: { idUtilisateur: null as any }
  };

  statuts = ['NOUVEAU', 'EN_COURS', 'EN_TEST', 'RESOLU', 'FERME'];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private ticketService: TicketService,
    private tacheService: TacheService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.user = this.authService.getUser();
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.chargerTicket(Number(id));
      this.chargerDeveloppeurs();
    }
  }

  chargerTicket(id: number): void {
    this.chargement = true;
    this.ticketService.getById(id).subscribe({
      next: (data) => {
        this.ticket = data;
        this.taches = data.taches || [];
        this.nouvelleTache.ticket.idTicket = data.idTicket;
        this.chargement = false;
      },
      error: (err: any) => {
        this.erreur = 'Erreur lors du chargement du ticket';
        this.chargement = false;
      }
    });
  }

  chargerDeveloppeurs(): void {
    this.ticketService.getApplications().subscribe({
      next: (_data: any) => {},
      error: (err: any) => {}
    });
  }

  changerStatut(statut: string): void {
    this.ticketService.changerStatut(this.ticket.idTicket, statut).subscribe({
      next: (_response: any) => {
        this.ticket.statut = statut;
        this.succes = 'Statut changé en ' + statut + ' avec succès';
        setTimeout(() => this.succes = '', 3000);
      },
      error: (err: any) => {
        this.erreur = 'Erreur lors du changement de statut';
        setTimeout(() => this.erreur = '', 3000);
      }
    });
  }

  creerTache(): void {
    if (!this.nouvelleTache.titre) {
      this.erreur = 'Le titre de la tâche est obligatoire';
      return;
    }
    this.tacheService.create(this.nouvelleTache).subscribe({
      next: (data: any) => {
        this.taches.push(data);
        this.succes = 'Tâche créée avec succès';
        this.afficherFormTache = false;
        this.nouvelleTache.titre = '';
        this.nouvelleTache.description = '';
        this.nouvelleTache.developpeur.idUtilisateur = null;
        setTimeout(() => this.succes = '', 3000);
      },
      error: (err: any) => {
        this.erreur = 'Erreur lors de la création de la tâche';
        setTimeout(() => this.erreur = '', 3000);
      }
    });
  }

  terminerTache(id: number): void {
    this.tacheService.terminer(id).subscribe({
      next: (data: any) => {
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

  getPrioriteClass(priorite: string): string {
    switch (priorite) {
      case 'BASSE': return 'badge-basse';
      case 'MOYENNE': return 'badge-moyenne';
      case 'HAUTE': return 'badge-haute';
      case 'CRITIQUE': return 'badge-critique';
      default: return '';
    }
  }

  getStatutClass(statut: string): string {
    switch (statut) {
      case 'NOUVEAU': return 'statut-nouveau';
      case 'EN_COURS': return 'statut-encours';
      case 'EN_TEST': return 'statut-entest';
      case 'RESOLU': return 'statut-resolu';
      case 'FERME': return 'statut-ferme';
      default: return '';
    }
  }

  retour(): void {
    this.router.navigate(['/admin/dashboard']);
  }
}