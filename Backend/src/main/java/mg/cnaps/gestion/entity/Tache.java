package mg.cnaps.gestion.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "TACHE")
public class Tache {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tache")
    @SequenceGenerator(name = "seq_tache", sequenceName = "SEQ_TACHE", allocationSize = 1)
    @Column(name = "ID_TACHE")
    private Long idTache;

    @Column(name = "TITRE", nullable = false, length = 200)
    private String titre;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    @Column(name = "STATUT", nullable = false, length = 20)
    private String statut;

    @Column(name = "PRIORITE", nullable = false, length = 20)
    private String priorite;

    @Column(name = "DATE_CREATION", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateCreation;

    @Column(name = "DATE_DEBUT")
    @Temporal(TemporalType.DATE)
    private Date dateDebut;

    @Column(name = "DATE_FIN_PREVUE")
    @Temporal(TemporalType.DATE)
    private Date dateFinPrevue;

    @Column(name = "DATE_FIN_REELLE")
    @Temporal(TemporalType.DATE)
    private Date dateFinReelle;

    @ManyToOne
    @JoinColumn(name = "ID_TICKET", nullable = false)
    @JsonIgnoreProperties({"taches", "historiques", "assigneAdmin", "createur"})
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "ID_DEVELOPPEUR")
    @JsonIgnoreProperties({"taches", "tickets", "motDePasse", "departement"})
    private Utilisateur developpeur;

    @ManyToOne
    @JoinColumn(name = "ANCIEN_DEVELOPPEUR")
    @JsonIgnoreProperties({"taches", "tickets", "motDePasse", "departement"})
    private Utilisateur ancienDeveloppeur;

    public Tache() {}

    public Tache(String titre, String description, String statut, String priorite,
                 Date dateCreation, Ticket ticket, Utilisateur developpeur) {
        this.titre = titre;
        this.description = description;
        this.statut = statut;
        this.priorite = priorite;
        this.dateCreation = dateCreation;
        this.ticket = ticket;
        this.developpeur = developpeur;
    }

    public Long getIdTache() { return idTache; }
    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public String getStatut() { return statut; }
    public String getPriorite() { return priorite; }
    public Date getDateCreation() { return dateCreation; }
    public Date getDateDebut() { return dateDebut; }
    public Date getDateFinPrevue() { return dateFinPrevue; }
    public Date getDateFinReelle() { return dateFinReelle; }
    public Ticket getTicket() { return ticket; }
    public Utilisateur getDeveloppeur() { return developpeur; }
    public Utilisateur getAncienDeveloppeur() { return ancienDeveloppeur; }

    public void setIdTache(Long idTache) { this.idTache = idTache; }
    public void setTitre(String titre) { this.titre = titre; }
    public void setDescription(String description) { this.description = description; }
    public void setStatut(String statut) { this.statut = statut; }
    public void setPriorite(String priorite) { this.priorite = priorite; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }
    public void setDateFinPrevue(Date dateFinPrevue) { this.dateFinPrevue = dateFinPrevue; }
    public void setDateFinReelle(Date dateFinReelle) { this.dateFinReelle = dateFinReelle; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }
    public void setDeveloppeur(Utilisateur developpeur) { this.developpeur = developpeur; }
    public void setAncienDeveloppeur(Utilisateur ancienDeveloppeur) { this.ancienDeveloppeur = ancienDeveloppeur; }
}