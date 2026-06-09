package mg.cnaps.gestion.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "HISTORIQUE")
public class Historique {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_hist")
    @SequenceGenerator(name = "seq_hist", sequenceName = "SEQ_HISTORIQUE", allocationSize = 1)
    @Column(name = "ID_HISTORIQUE")
    private Long idHistorique;

    @Column(name = "TYPE_ACTION", nullable = false, length = 50)
    private String typeAction;

    @Column(name = "DESCRIPTION_ACTION", nullable = false, length = 500)
    private String descriptionAction;

    @Column(name = "ANCIENNE_VALEUR", length = 255)
    private String ancienneValeur;

    @Column(name = "NOUVELLE_VALEUR", length = 255)
    private String nouvelleValeur;

    @Column(name = "DATE_ACTION", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAction;

    @ManyToOne
    @JoinColumn(name = "ID_TICKET", nullable = true)
    @JsonIgnoreProperties({"taches", "historiques", "assigneAdmin", "createur", "application"})
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "ID_UTILISATEUR", nullable = true)
    @JsonIgnoreProperties({"taches", "tickets", "motDePasse", "departement"})
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "ID_TACHE")
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnoreProperties({"ticket", "historiques", "developpeur"})
    private Tache tache;

    public Historique() {}

    public Historique(String typeAction, String descriptionAction, String ancienneValeur,
                      String nouvelleValeur, Date dateAction, Ticket ticket,
                      Utilisateur utilisateur, Tache tache) {
        this.typeAction = typeAction;
        this.descriptionAction = descriptionAction;
        this.ancienneValeur = ancienneValeur;
        this.nouvelleValeur = nouvelleValeur;
        this.dateAction = dateAction;
        this.ticket = ticket;
        this.utilisateur = utilisateur;
        this.tache = tache;
    }

    public Long getIdHistorique() { return idHistorique; }
    public String getTypeAction() { return typeAction; }
    public String getDescriptionAction() { return descriptionAction; }
    public String getAncienneValeur() { return ancienneValeur; }
    public String getNouvelleValeur() { return nouvelleValeur; }
    public Date getDateAction() { return dateAction; }
    public Ticket getTicket() { return ticket; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public Tache getTache() { return tache; }

    public void setIdHistorique(Long idHistorique) { this.idHistorique = idHistorique; }
    public void setTypeAction(String typeAction) { this.typeAction = typeAction; }
    public void setDescriptionAction(String descriptionAction) { this.descriptionAction = descriptionAction; }
    public void setAncienneValeur(String ancienneValeur) { this.ancienneValeur = ancienneValeur; }
    public void setNouvelleValeur(String nouvelleValeur) { this.nouvelleValeur = nouvelleValeur; }
    public void setDateAction(Date dateAction) { this.dateAction = dateAction; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    public void setTache(Tache tache) { this.tache = tache; }
}