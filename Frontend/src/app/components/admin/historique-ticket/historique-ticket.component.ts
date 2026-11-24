import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HistoriqueService } from 'src/app/services/historique.service';
import { TicketService } from 'src/app/services/ticket.service';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-historique-ticket',
  templateUrl: './historique-ticket.component.html',
  styleUrls: ['./historique-ticket.component.css']
})
export class HistoriqueTicketComponent implements OnInit {

  ticket: any = null;
  historiques: any[] = [];
  chargement: boolean = false;
  erreur: string = '';
  user: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private historiqueService: HistoriqueService,
    private ticketService: TicketService,
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
      this.chargerHistorique(Number(id));
    }
  }

  chargerTicket(id: number): void {
    this.ticketService.getById(id).subscribe({
      next: (data) => {
        this.ticket = data;
      },
      error: (err: any) => {
        this.erreur = 'Erreur lors du chargement du ticket';
      }
    });
  }

  chargerHistorique(id: number): void {
    this.chargement = true;
    this.historiqueService.getByTicket(id).subscribe({
      next: (data) => {
        this.historiques = data;
        this.chargement = false;
      },
      error: (err: any) => {
        this.erreur = 'Erreur lors du chargement de l\'historique';
        this.chargement = false;
      }
    });
  }

  getTypeActionClass(type: string): string {
    switch (type) {
      case 'CREATION':          return 'action-creation';
      case 'MODIFICATION':      return 'action-modification';
      case 'CHANGEMENT_STATUT': return 'action-statut';
      case 'ASSIGNATION':       return 'action-assignation';
      case 'RESOLUTION':        return 'action-resolution';
      case 'FERMETURE':         return 'action-fermeture';
      case 'RAPPORT':           return 'action-rapport';   // ← NOUVEAU
      default:                  return '';
    }
  }

  getTypeActionIcon(type: string): string {
    switch (type) {
      case 'CREATION':          return 'add_circle';
      case 'MODIFICATION':      return 'edit';
      case 'CHANGEMENT_STATUT': return 'swap_horiz';
      case 'ASSIGNATION':       return 'person_add';
      case 'RESOLUTION':        return 'check_circle';
      case 'FERMETURE':         return 'lock';
      case 'RAPPORT':           return 'edit_note';        // ← NOUVEAU
      default:                  return 'history';
    }
  }

  retour(): void {
    this.router.navigate(['/admin/dashboard']);
  }
}