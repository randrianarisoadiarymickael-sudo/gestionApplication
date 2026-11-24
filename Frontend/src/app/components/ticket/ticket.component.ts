import { Component, OnInit, HostListener, ElementRef } from '@angular/core';
import { Router } from '@angular/router';
import { TicketService } from '../../services/ticket.service';
import { AuthService } from '../../services/auth.service';
import { UploadService } from '../../services/upload.service';

@Component({
  selector: 'app-ticket',
  templateUrl: './ticket.component.html',
  styleUrls: ['./ticket.component.css']
})
export class TicketComponent implements OnInit {

  applications: any[] = [];
  applicationsFiltrees: any[] = [];
  rechercheApplication: string = '';
  applicationSelectionnee: any = null;
  afficherListeApp: boolean = false;

  erreur: string = '';
  succes: string = '';
  chargement: boolean = false;
  chargementDemandes: boolean = false;
  user: any;
  dateAujourdhui: string = new Date().toLocaleDateString('fr-FR');
  ongletActif: string = 'nouveau';
  mesDemandes: any[] = [];
  imageFichier: File | null = null;
  imagePreview: string | null = null;

  ticket = {
    titre: '',
    description: '',
    priorite: 'MOYENNE',
    statut: 'NOUVEAU',
    imagePath: '' as string,
    application: { idApplication: null as any },
    createur: { idUtilisateur: null as any }
  };

  constructor(
    private ticketService: TicketService,
    private authService: AuthService,
    private uploadService: UploadService,
    private router: Router,
    private elRef: ElementRef
  ) {}

  ngOnInit(): void {
    this.user = this.authService.getUser();
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }
    this.ticket.createur.idUtilisateur = this.user.idUtilisateur;
    this.chargerApplications();
  }

  // Ferme la liste si clic en dehors
  @HostListener('document:click', ['$event'])
  onClickExterieur(event: MouseEvent): void {
    const cible = event.target as HTMLElement;
    if (!this.elRef.nativeElement.querySelector('.app-search-wrapper')?.contains(cible)) {
      this.afficherListeApp = false;
    }
  }

  chargerApplications(): void {
    this.ticketService.getApplications().subscribe({
      next: (data: any) => {
        this.applications = data;
        this.applicationsFiltrees = data;
      },
      error: () => { this.erreur = 'Erreur lors du chargement des applications'; }
    });
  }

  onRechercheApplication(valeur: string): void {
    this.rechercheApplication = valeur;
    this.afficherListeApp = true;
    this.applicationSelectionnee = null;
    this.ticket.application.idApplication = null;

    const terme = valeur.trim().toLowerCase();
    if (terme === '') {
      this.applicationsFiltrees = this.applications;
    } else {
      this.applicationsFiltrees = this.applications.filter(app =>
        app.nomApplication.toLowerCase().includes(terme)
      );
    }
  }

  selectionnerApplication(app: any): void {
    this.applicationSelectionnee = app;
    this.rechercheApplication = app.nomApplication;
    this.ticket.application.idApplication = app.idApplication;
    this.afficherListeApp = false;
  }

  ouvrirListeApp(): void {
    this.applicationsFiltrees = this.applications;
    this.afficherListeApp = true;
  }

  effacerApplication(event: Event): void {
    event.stopPropagation();
    this.applicationSelectionnee = null;
    this.rechercheApplication = '';
    this.ticket.application.idApplication = null;
    this.applicationsFiltrees = this.applications;
    this.afficherListeApp = false;
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.traiterFichier(file);
    }
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    const file = event.dataTransfer?.files[0];
    if (file && file.type.startsWith('image/')) {
      this.traiterFichier(file);
    }
  }

  traiterFichier(file: File): void {
    if (file.size > 5 * 1024 * 1024) {
      this.erreur = 'L\'image ne doit pas dépasser 5 Mo';
      return;
    }
    this.imageFichier = file;
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.imagePreview = e.target.result;
    };
    reader.readAsDataURL(file);
  }

  supprimerImage(event: Event): void {
    event.stopPropagation();
    this.imageFichier = null;
    this.imagePreview = null;
    this.ticket.imagePath = '';
  }

  creerTicket(): void {
    this.erreur = '';
    this.succes = '';
    this.chargement = true;

    if (!this.ticket.titre || !this.ticket.description || !this.ticket.application.idApplication) {
      this.erreur = 'Veuillez remplir tous les champs obligatoires';
      this.chargement = false;
      return;
    }

    if (this.imageFichier) {
      this.uploadService.uploadImage(this.imageFichier).subscribe({
        next: (res: any) => {
          this.ticket.imagePath = res.imagePath;
          this.soumettreTiket();
        },
        error: () => {
          this.erreur = 'Erreur lors de l\'upload de l\'image';
          this.chargement = false;
        }
      });
    } else {
      this.soumettreTiket();
    }
  }

  soumettreTiket(): void {
    this.ticketService.create(this.ticket).subscribe({
      next: () => {
        this.succes = 'Déclaration soumise avec succès !';
        this.chargement = false;
        this.ticket.titre = '';
        this.ticket.description = '';
        this.ticket.priorite = 'MOYENNE';
        this.ticket.application.idApplication = null;
        this.ticket.imagePath = '';
        this.imageFichier = null;
        this.imagePreview = null;
        this.rechercheApplication = '';
        this.applicationSelectionnee = null;
        setTimeout(() => { this.succes = ''; }, 3000);
      },
      error: () => {
        this.erreur = 'Erreur lors de la création du ticket';
        this.chargement = false;
      }
    });
  }

  basculerMesDemandes(): void {
    this.ongletActif = 'mesdemandes';
    this.chargerMesDemandes();
  }

  chargerMesDemandes(): void {
    this.chargementDemandes = true;
    this.ticketService.getAll().subscribe({
      next: (data: any) => {
        this.mesDemandes = data
          .filter((t: any) => t.createur?.idUtilisateur === this.user?.idUtilisateur)
          .sort((a: any, b: any) => b.idTicket - a.idTicket);
        this.chargementDemandes = false;
      },
      error: () => {
        this.chargementDemandes = false;
      }
    });
  }

  getPrioriteClass(priorite: string): string {
    switch (priorite) {
      case 'BASSE': return 'priorite-basse';
      case 'MOYENNE': return 'priorite-moyenne';
      case 'HAUTE': return 'priorite-haute';
      case 'CRITIQUE': return 'priorite-critique';
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

  getStatutLabel(statut: string): string {
    switch (statut) {
      case 'NOUVEAU': return 'Nouveau';
      case 'EN_COURS': return 'En cours';
      case 'EN_TEST': return 'En test';
      case 'RESOLU': return 'Résolu';
      default: return statut;
    }
  }

  getRowClass(statut: string): string {
    switch (statut) {
      case 'RESOLU': return 'row-resolu';
      case 'EN_COURS': return 'row-encours';
      case 'EN_TEST': return 'row-entest';
      case 'NOUVEAU': return 'row-nouveau';
      default: return '';
    }
  }

  annuler(): void {
    this.router.navigate(['/dashboard']);
  }
  getIdInconnue(): number | null {
  const inconnue = this.applications.find(
    a => a.nomApplication?.toLowerCase() === 'inconnue'
  );
  return inconnue ? inconnue.idApplication : null;
  }
}