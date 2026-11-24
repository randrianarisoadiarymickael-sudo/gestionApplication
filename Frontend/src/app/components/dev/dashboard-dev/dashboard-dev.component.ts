import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { TacheService } from 'src/app/services/tache.service';
import { AuthService } from 'src/app/services/auth.service';
import { TicketService } from 'src/app/services/ticket.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-dashboard-dev',
  templateUrl: './dashboard-dev.component.html',
  styleUrls: ['./dashboard-dev.component.css']
})
export class DashboardDevComponent implements OnInit {

  // Sidebar
  sidebarOuverte: boolean = true;

  // Onglet actif
  ongletActif: string = 'mes-taches';

  taches: any[] = [];
  tachesFiltrees: any[] = [];
  toutesLesTaches: any[] = [];

  tachesReassignees: any[] = [];
  pageReassignee: number = 0;
  taillePageReassignee: number = 10;
  totalPagesReassignee: number = 0;
  totalElementsReassignee: number = 0;
  chargementReassignee: boolean = false;

  chargement: boolean = false;
  erreur: string = '';
  user: any;
  filtreStatut: string = 'TOUS';
  recherche: string = '';
  tacheSelectionnee: any = null;
  afficherModal: boolean = false;
  tacheSelectionneeEstReassignee: boolean = false;
  imageEnPleinEcran: string | null = null;

  pageActuelle: number = 0;
  taillePage: number = 10;
  totalPages: number = 0;
  totalElements: number = 0;

  statuts = ['TOUS', 'EN_ATTENTE', 'EN_COURS', 'EN_TEST', 'TERMINEE'];

  rapportTexte: string = '';
  rapportEnvoi: boolean = false;
  rapportSucces: boolean = false;
  rapportErreur: boolean = false;
  historiquesTache: any[] = [];
  chargementHistorique: boolean = false;

  // Renommage application
  afficherRenommageApp: boolean = false;
  nouveauNomApp: string = '';
  renommageEnCours: boolean = false;
  renommageSucces: string = '';
  renommageErreur: string = '';

  constructor(
    private tacheService: TacheService,
    private authService: AuthService,
    private router: Router,
    private http: HttpClient,
    private ticketService: TicketService
  ) {}

  ngOnInit(): void {
    this.user = this.authService.getUser();
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }
    if (this.user.role !== 'DEV') {
      this.router.navigate(['/login']);
      return;
    }
    this.chargerTaches();
    this.chargerToutesTaches();
    this.chargerTachesReassignees();
  }

  // ── Sidebar ─────────────────────────────────────────────────────

  toggleSidebar(): void {
    this.sidebarOuverte = !this.sidebarOuverte;
  }

  // ── Onglets ─────────────────────────────────────────────────────

  changerOnglet(onglet: string): void {
    this.ongletActif = onglet;
  }

  // ── Tâches actives ──────────────────────────────────────────────

  chargerTaches(): void {
    this.chargement = true;
    this.tacheService.getByDeveloppeurPagine(
      this.user.idUtilisateur,
      this.pageActuelle,
      this.taillePage
    ).subscribe({
      next: (data: any) => {
        this.taches = data.content;
        this.tachesFiltrees = data.content;
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;
        this.chargement = false;
        this.onRecherche();
      },
      error: () => {
        this.erreur = 'Erreur lors du chargement des tâches';
        this.chargement = false;
      }
    });
  }

  chargerToutesTaches(): void {
    this.tacheService.getByDeveloppeur(this.user.idUtilisateur).subscribe({
      next: (data: any[]) => { this.toutesLesTaches = data; },
      error: () => { console.error('Erreur chargement toutes les tâches'); }
    });
  }

  // ── Tâches réassignées ──────────────────────────────────────────

  chargerTachesReassignees(): void {
    this.chargementReassignee = true;
    const token = this.authService.getUser()?.token;
    const headers = token
      ? new HttpHeaders({ Authorization: `Bearer ${token}` })
      : new HttpHeaders();

    this.http.get<any>(
      `/api/taches/ancien-developpeur/${this.user.idUtilisateur}/pagine?page=${this.pageReassignee}&size=${this.taillePageReassignee}`,
      { headers }
    ).subscribe({
      next: (data: any) => {
        this.tachesReassignees = data.content;
        this.totalPagesReassignee = data.totalPages;
        this.totalElementsReassignee = data.totalElements;
        this.chargementReassignee = false;
      },
      error: () => {
        console.error('Erreur chargement tâches réassignées');
        this.chargementReassignee = false;
      }
    });
  }

  pageSuivanteReassignee(): void {
    if (this.pageReassignee < this.totalPagesReassignee - 1) {
      this.pageReassignee++;
      this.chargerTachesReassignees();
    }
  }

  pagePrecedenteReassignee(): void {
    if (this.pageReassignee > 0) {
      this.pageReassignee--;
      this.chargerTachesReassignees();
    }
  }

  // ── Pagination ──────────────────────────────────────────────────

  pageSuivante(): void {
    if (this.pageActuelle < this.totalPages - 1) {
      this.pageActuelle++;
      this.chargerTaches();
    }
  }

  pagePrecedente(): void {
    if (this.pageActuelle > 0) {
      this.pageActuelle--;
      this.chargerTaches();
    }
  }

  allerPage(page: number): void {
    this.pageActuelle = page;
    this.chargerTaches();
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

  // ── Filtres & Recherche ─────────────────────────────────────────

  filtrer(statut: string): void {
    this.filtreStatut = statut;
    this.onRecherche();
  }

  onRecherche(): void {
    const r = this.recherche.trim().toLowerCase();
    const base = this.filtreStatut === 'TOUS'
      ? this.taches
      : this.taches.filter(t => t.statut === this.filtreStatut);

    if (r === '') {
      this.tachesFiltrees = base;
    } else {
      this.tachesFiltrees = base.filter(t =>
        t.titre?.toLowerCase().includes(r) ||
        t.ticket?.titre?.toLowerCase().includes(r) ||
        t.ticket?.application?.nomApplication?.toLowerCase().includes(r)
      );
    }
  }

  // ── Modal ───────────────────────────────────────────────────────

  voirTache(tache: any, estReassignee: boolean = false): void {
    this.tacheSelectionneeEstReassignee = estReassignee;
    this.afficherModal = true;
    this.rapportTexte = '';
    this.rapportSucces = false;
    this.rapportErreur = false;
    this.rapportEnvoi = false;
    this.historiquesTache = [];
    this.afficherRenommageApp = false;
    this.nouveauNomApp = '';
    this.renommageSucces = '';
    this.renommageErreur = '';

    this.tacheService.getById(tache.idTache).subscribe({
      next: (data: any) => {
        this.tacheSelectionnee = data;
        this.chargerHistoriqueTache(data.idTache);
      },
      error: () => {
        this.tacheSelectionnee = tache;
        this.chargerHistoriqueTache(tache.idTache);
      }
    });
  }

  fermerModal(): void {
    this.afficherModal = false;
    this.tacheSelectionnee = null;
    this.tacheSelectionneeEstReassignee = false;
    this.afficherRenommageApp = false;
    this.nouveauNomApp = '';
    this.renommageSucces = '';
    this.renommageErreur = '';
  }

  chargerHistoriqueTache(idTache: number): void {
    this.chargementHistorique = true;
    const token = this.authService.getUser()?.token;
    const headers = token
      ? new HttpHeaders({ Authorization: `Bearer ${token}` })
      : new HttpHeaders();

    this.http.get<any[]>(`/api/taches/${idTache}/historique`, { headers }).subscribe({
      next: (data) => {
        this.historiquesTache = data.sort((a, b) =>
          new Date(b.dateAction).getTime() - new Date(a.dateAction).getTime()
        );
        this.chargementHistorique = false;
      },
      error: () => { this.chargementHistorique = false; }
    });
  }

  get rapportsPrecedents(): any[] {
    return this.historiquesTache.filter(h => h.typeAction === 'RAPPORT');
  }

  soumettreRapport(): void {
    if (!this.rapportTexte?.trim() || !this.tacheSelectionnee) return;
    this.rapportEnvoi = true;
    this.rapportSucces = false;
    this.rapportErreur = false;

    this.tacheService.ajouterRapport(
      this.tacheSelectionnee.idTache,
      this.rapportTexte,
      this.user.idUtilisateur
    ).subscribe({
      next: () => {
        this.rapportSucces = true;
        this.rapportTexte = '';
        this.rapportEnvoi = false;
        this.chargerHistoriqueTache(this.tacheSelectionnee.idTache);
        setTimeout(() => this.rapportSucces = false, 4000);
      },
      error: () => {
        this.rapportErreur = true;
        this.rapportEnvoi = false;
        setTimeout(() => this.rapportErreur = false, 4000);
      }
    });
  }

  // ── Renommage application ────────────────────────────────────────

  ouvrirRenommageApp(): void {
    this.nouveauNomApp = this.tacheSelectionnee?.ticket?.application?.nomApplication || '';
    this.afficherRenommageApp = true;
    this.renommageSucces = '';
    this.renommageErreur = '';
  }

  fermerRenommageApp(): void {
    this.afficherRenommageApp = false;
    this.nouveauNomApp = '';
    this.renommageSucces = '';
    this.renommageErreur = '';
  }

  confirmerRenommageApp(): void {
    if (!this.nouveauNomApp?.trim()) {
      this.renommageErreur = 'Le nom ne peut pas être vide';
      return;
    }
    const idApp = this.tacheSelectionnee?.ticket?.application?.idApplication;
    if (!idApp) {
      this.renommageErreur = 'Impossible d\'identifier l\'application (ID manquant)';
      return;
    }
    this.renommageEnCours = true;
    this.renommageErreur = '';

    this.ticketService.renommerApplication(idApp, this.nouveauNomApp.trim()).subscribe({
      next: () => {
        this.renommageSucces = 'Application renommée avec succès';
        this.renommageEnCours = false;
        this.tacheSelectionnee.ticket.application.nomApplication = this.nouveauNomApp.trim();
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

  // ── Actions ─────────────────────────────────────────────────────

  commencerTache(id: number): void {
    this.tacheService.changerStatut(id, 'EN_COURS', this.user.idUtilisateur).subscribe({
      next: () => { this.chargerTaches(); this.fermerModal(); },
      error: (err) => {
        if (err.status === 403) alert('Accès refusé : vous n\'êtes plus assigné à cette tâche.');
      }
    });
  }

  mettreEnTest(id: number): void {
    this.tacheService.changerStatut(id, 'EN_TEST', this.user.idUtilisateur).subscribe({
      next: () => { this.chargerTaches(); this.fermerModal(); },
      error: (err) => {
        if (err.status === 403) alert('Accès refusé : vous n\'êtes plus assigné à cette tâche.');
      }
    });
  }

  terminerTache(id: number): void {
    this.tacheService.terminer(id, this.user.idUtilisateur).subscribe({
      next: () => { this.chargerTaches(); this.fermerModal(); },
      error: (err) => {
        if (err.status === 403) alert('Accès refusé : vous n\'êtes plus assigné à cette tâche.');
      }
    });
  }

  // ── Utilitaires ─────────────────────────────────────────────────

  getNombreParStatut(statut: string): number {
    if (statut === 'TOUS') return this.toutesLesTaches.length;
    return this.toutesLesTaches.filter(t => t.statut === statut).length;
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

  getPrioriteClass(priorite: string): string {
    switch (priorite) {
      case 'BASSE':    return 'badge-basse';
      case 'MOYENNE':  return 'badge-moyenne';
      case 'HAUTE':    return 'badge-haute';
      case 'CRITIQUE': return 'badge-critique';
      default:         return '';
    }
  }

  getStatutClass(statut: string): string {
    switch (statut) {
      case 'EN_ATTENTE': return 'statut-attente';
      case 'EN_COURS':   return 'statut-encours';
      case 'EN_TEST':    return 'statut-entest';
      case 'TERMINEE':   return 'statut-terminee';
      default:           return '';
    }
  }

  getStatutLabel(statut: string): string {
    switch (statut) {
      case 'EN_ATTENTE': return 'En attente';
      case 'EN_COURS':   return 'En cours';
      case 'EN_TEST':    return 'En test';
      case 'TERMINEE':   return 'Terminée';
      default:           return statut;
    }
  }

  deconnecter(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}