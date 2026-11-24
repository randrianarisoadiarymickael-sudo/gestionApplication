import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TicketService } from 'src/app/services/ticket.service';
import { AuthService } from 'src/app/services/auth.service';
import { TacheService } from 'src/app/services/tache.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-dashboard-admin',
  templateUrl: './dashboard-admin.component.html',
  styleUrls: ['./dashboard-admin.component.css']
})
export class DashboardAdminComponent implements OnInit {

  tickets: any[] = [];
  ticketsFiltres: any[] = [];
  chargement: boolean = false;
  erreur: string = '';
  filtreStatut: string = 'TOUS';
  recherche: string = '';
  user: any;
  ticketSelectionne: any = null;
  afficherModal: boolean = false;
  afficherModalTache: boolean = false;
  developpeurs: any[] = [];
  tachesTicket: any[] = [];
  tachesParTicket: { [key: number]: string } = {};
  imageEnPleinEcran: string | null = null;

  pageActuelle: number = 0;
  taillePage: number = 10;
  totalPages: number = 0;
  totalElements: number = 0;

  // ← ajouté
  statsStatut: any = { NOUVEAU: 0, EN_COURS: 0, EN_TEST: 0, RESOLU: 0 };

  nouvelleTache = {
    titre: '',
    description: '',
    priorite: 'MOYENNE',
    ticket: { idTicket: null as any },
    developpeur: { idUtilisateur: null as any }
  };

  statuts = ['TOUS', 'NOUVEAU', 'EN_COURS', 'EN_TEST', 'RESOLU'];

  constructor(
    private ticketService: TicketService,
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
    if (this.user.role !== 'ADMIN') {
      this.router.navigate(['/dashboard']);
      return;
    }
    this.chargerTickets();
  }

  chargerTickets(): void {
    this.chargement = true;
    this.ticketService.getAllPagine(this.pageActuelle, this.taillePage).subscribe({
      next: (data) => {
        this.tickets = data.content;
        this.ticketsFiltres = data.content;
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;
        this.chargement = false;
        this.chargerTousLesDeveloppeurs();
        this.chargerStatsStatut();               // ← ajouté
      },
      error: () => {
        this.erreur = 'Erreur lors du chargement des tickets';
        this.chargement = false;
      }
    });
  }

  // ← ajouté
  chargerStatsStatut(): void {
    this.ticketService.getStatsStatut().subscribe({
      next: (data) => { this.statsStatut = data; },
      error: () => {}
    });
  }

  pageSuivante(): void {
    if (this.pageActuelle < this.totalPages - 1) {
      this.pageActuelle++;
      this.chargerTickets();
    }
  }

  pagePrecedente(): void {
    if (this.pageActuelle > 0) {
      this.pageActuelle--;
      this.chargerTickets();
    }
  }

  allerPage(page: number): void {
    this.pageActuelle = page;
    this.chargerTickets();
  }

  getPages(): number[] {
    const pages = [];
    const debut = Math.max(0, this.pageActuelle - 2);
    const fin = Math.min(this.totalPages - 1, this.pageActuelle + 2);
    for (let i = debut; i <= fin; i++) {
      pages.push(i);
    }
    return pages;
  }

  chargerTousLesDeveloppeurs(): void {
    this.tachesParTicket = {};
    this.tickets.forEach(ticket => {
      this.tacheService.getByTicket(ticket.idTicket).subscribe({
        next: (taches: any[]) => {
          if (taches && taches.length > 0) {
            const dev = taches[0].developpeur;
            if (dev) {
              this.tachesParTicket[ticket.idTicket] = dev.nom + ' ' + dev.prenom;
            }
          }
        },
        error: () => {}
      });
    });
  }

  getDevTicket(idTicket: number): string | null {
    return this.tachesParTicket[idTicket] || null;
  }

  filtrer(statut: string): void {
    this.filtreStatut = statut;
    this.pageActuelle = 0;
    this.appliquerFiltres();
  }

  appliquerFiltres(): void {
    let resultats = this.tickets;
    if (this.filtreStatut !== 'TOUS') {
      resultats = resultats.filter(t => t.statut === this.filtreStatut);
    }
    if (this.recherche.trim() !== '') {
      const r = this.recherche.toLowerCase();
      resultats = resultats.filter(t =>
        t.titre.toLowerCase().includes(r) ||
        t.createur?.nom.toLowerCase().includes(r) ||
        t.application?.nomApplication.toLowerCase().includes(r)
      );
    }
    this.ticketsFiltres = resultats;
  }

  onRecherche(): void {
    this.appliquerFiltres();
  }

  voirTicket(id: number): void {
    this.ouvrirModal(id);
  }

  ouvrirModal(id: number): void {
    this.ticketService.getById(id).subscribe({
      next: (data) => {
        this.ticketSelectionne = data;
        this.afficherModal = true;
        this.chargerTachesTicket(data.idTicket);
      },
      error: () => {
        this.erreur = 'Erreur lors du chargement du ticket';
      }
    });
  }

  fermerModal(): void {
    this.afficherModal = false;
    this.ticketSelectionne = null;
    this.tachesTicket = [];
  }

  ouvrirModalTache(): void {
    this.nouvelleTache = {
      titre: '',
      description: '',
      priorite: 'MOYENNE',
      ticket: { idTicket: this.ticketSelectionne?.idTicket },
      developpeur: { idUtilisateur: null }
    };
    this.afficherModalTache = true;
    this.chargerDeveloppeurs();
    this.chargerTachesTicket(this.ticketSelectionne?.idTicket);
  }

  fermerModalTache(): void {
    this.afficherModalTache = false;
  }

  changerStatutModal(statut: string): void {
    if (statut === 'TOUS') return;
    this.ticketService.changerStatut(this.ticketSelectionne.idTicket, statut).subscribe({
      next: () => {
        this.ticketSelectionne.statut = statut;
        const index = this.tickets.findIndex(t => t.idTicket === this.ticketSelectionne.idTicket);
        if (index !== -1) {
          this.tickets[index].statut = statut;
        }
        this.appliquerFiltres();
        this.chargerStatsStatut();               // ← ajouté : refresh stats après changement statut
      },
      error: () => {
        this.erreur = 'Erreur lors du changement de statut';
      }
    });
  }

  chargerDeveloppeurs(): void {
    this.tacheService.getDeveloppeurs().subscribe({
      next: (data) => { this.developpeurs = data; },
      error: () => {}
    });
  }

  chargerTachesTicket(idTicket: number): void {
    this.tacheService.getByTicket(idTicket).subscribe({
      next: (data) => {
        this.tachesTicket = data;
        if (data && data.length > 0 && data[0].developpeur) {
          this.tachesParTicket[idTicket] =
            data[0].developpeur.nom + ' ' + data[0].developpeur.prenom;
        }
      },
      error: () => {}
    });
  }

  assignerTacheDepuisModal(): void {
    if (!this.nouvelleTache.developpeur.idUtilisateur) {
      this.erreur = 'Veuillez choisir un développeur';
      return;
    }

    const tacheActive = this.tachesTicket.find(t => t.statut !== 'TERMINEE');

    if (tacheActive) {
      this.tacheService.reassigner(
        tacheActive.idTache,
        this.nouvelleTache.developpeur.idUtilisateur
      ).subscribe({
        next: () => {
          this.chargerTachesTicket(this.ticketSelectionne?.idTicket);
          this.chargerTousLesDeveloppeurs();
          this.nouvelleTache.developpeur.idUtilisateur = null;
          this.erreur = '';
        },
        error: () => {
          this.erreur = 'Erreur lors de la réassignation';
        }
      });
    } else {
      const tacheAEnvoyer = {
        titre: 'Tâche #' + this.ticketSelectionne?.idTicket,
        description: '',
        priorite: this.ticketSelectionne?.priorite || 'MOYENNE',
        ticket: { idTicket: this.ticketSelectionne?.idTicket },
        developpeur: { idUtilisateur: this.nouvelleTache.developpeur.idUtilisateur }
      };

      this.tacheService.create(tacheAEnvoyer).subscribe({
        next: () => {
          this.chargerTachesTicket(this.ticketSelectionne?.idTicket);
          this.chargerTousLesDeveloppeurs();
          this.nouvelleTache.developpeur.idUtilisateur = null;
          this.erreur = '';
        },
        error: () => {
          this.erreur = 'Erreur lors de l\'assignation';
        }
      });
    }
  }

  // ← modifié : utilise statsStatut au lieu de filtrer this.tickets
  getNombreParStatut(statut: string): number {
    if (statut === 'TOUS') return this.totalElements;
    return this.statsStatut[statut] ?? 0;
  }

  getNombreParPriorite(priorite: string): number {
    return this.tickets.filter(t => t.priorite === priorite).length;
  }

  getPourcentagePriorite(priorite: string): number {
    const total = this.tickets.length;
    if (total === 0) return 0;
    return Math.round((this.getNombreParPriorite(priorite) / total) * 100);
  }

  getTauxResolution(): number {
    const total = this.totalElements;
    if (total === 0) return 0;
    return Math.round(((this.statsStatut['RESOLU'] ?? 0) / total) * 100);
  }

  getTopApplications(): any[] {
    const map: any = {};
    this.tickets.forEach(t => {
      const nom = t.application?.nomApplication || 'Inconnue';
      map[nom] = (map[nom] || 0) + 1;
    });
    return Object.keys(map)
      .map(nom => ({ nom, count: map[nom] }))
      .sort((a, b) => b.count - a.count)
      .slice(0, 4);
  }

  getAppColor(index: number): string {
    const colors = ['#1a3c8f', '#2e7d32', '#c0392b', '#e65100', '#f4a700'];
    return colors[index % colors.length];
  }

  getTacheStatutClass(statut: string): string {
    switch (statut) {
      case 'EN_ATTENTE': return 'statut-nouveau';
      case 'EN_COURS':   return 'statut-encours';
      case 'TERMINEE':   return 'statut-resolu';
      default: return '';
    }
  }

  getPrioriteClass(priorite: string): string {
    switch (priorite) {
      case 'BASSE':    return 'badge-basse';
      case 'MOYENNE':  return 'badge-moyenne';
      case 'HAUTE':    return 'badge-haute';
      case 'CRITIQUE': return 'badge-critique';
      default: return '';
    }
  }

  getStatutClass(statut: string): string {
    switch (statut) {
      case 'NOUVEAU':  return 'statut-nouveau';
      case 'EN_COURS': return 'statut-encours';
      case 'EN_TEST':  return 'statut-entest';
      case 'RESOLU':   return 'statut-resolu';
      default: return '';
    }
  }

  getImageUrl(imagePath: string): string {
    if (!imagePath) return '';
    if (imagePath.startsWith('http')) return imagePath;
    return `${environment.uploadsUrl}/${imagePath}`;
  }

  ouvrirImagePleinEcran(imagePath: string): void {
    this.imageEnPleinEcran = this.getImageUrl(imagePath);
  }

  fermerImagePleinEcran(): void {
    this.imageEnPleinEcran = null;
  }

  deconnecter(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  voirHistorique(id: number): void {
    this.fermerModal();
    this.router.navigate(['/admin/historique', id]);
  }

  // Renommage application
afficherRenommageApp: boolean = false;
nouveauNomApp: string = '';
renommageEnCours: boolean = false;
renommageSucces: string = '';
renommageErreur: string = '';

ouvrirRenommageApp(): void {
  this.nouveauNomApp = this.ticketSelectionne?.application?.nomApplication || '';
  this.afficherRenommageApp = true;
  this.renommageSucces = '';
  this.renommageErreur = '';
}

fermerRenommageApp(): void {
  this.afficherRenommageApp = false;
  this.nouveauNomApp = '';
}

confirmerRenommageApp(): void {
  if (!this.nouveauNomApp?.trim()) {
    this.renommageErreur = 'Le nom ne peut pas être vide';
    return;
  }
  this.renommageEnCours = true;
  this.renommageErreur = '';
  const idApp = this.ticketSelectionne?.application?.idApplication;

  this.ticketService.renommerApplication(idApp, this.nouveauNomApp.trim()).subscribe({
    next: () => {
      this.renommageSucces = 'Application renommée avec succès';
      this.renommageEnCours = false;
      this.ticketSelectionne.application.nomApplication = this.nouveauNomApp.trim();
      const t = this.tickets.find(t => t.idTicket === this.ticketSelectionne.idTicket);
      if (t) t.application.nomApplication = this.nouveauNomApp.trim();
      this.appliquerFiltres();
      setTimeout(() => {
        this.renommageSucces = '';
        this.afficherRenommageApp = false;
      }, 2000);
    },
    error: () => {
      this.renommageErreur = 'Erreur lors du renommage';
      this.renommageEnCours = false;
    }
  });
}
}