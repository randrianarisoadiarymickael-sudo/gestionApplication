package mg.cnaps.gestion.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "TICKET")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_ticket")
    @SequenceGenerator(name = "seq_ticket", sequenceName = "SEQ_TICKET", allocationSize = 1)
    @Column(name = "ID_TICKET")
    private Long idTicket;

    @Column(name = "TITRE", nullable = false, length = 200)
    private String titre;

    @Column(name = "DESCRIPTION", nullable = false, length = 255)
    private String description;

    @Column(name = "STATUT", nullable = false, length = 20)
    private String statut;

    @Column(name = "PRIORITE", nullable = false, length = 20)
    private String priorite;

    @Column(name = "IMAGE_PATH", length = 500)
    private String imagePath;

    @Column(name = "DATE_CREATION", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateCreation;

    @Column(name = "DATE_MODIFICATION")
    @Temporal(TemporalType.DATE)
    private Date dateModification;

    @Column(name = "DATE_RESOLUTION")
    @Temporal(TemporalType.DATE)
    private Date dateResolution;

    @Column(name = "DATE_FERMETURE")
    @Temporal(TemporalType.DATE)
    private Date dateFermeture;

    @ManyToOne
    @JoinColumn(name = "ID_APPLICATION", nullable = false)
    @JsonIgnoreProperties({"tickets"})
    private Application application;

    @ManyToOne
    @JoinColumn(name = "ID_CREATEUR", nullable = false)
    @JsonIgnoreProperties({"tickets", "taches", "motDePasse", "departement"})
    private Utilisateur createur;

    @ManyToOne
    @JoinColumn(name = "ID_ASSIGNE_ADMIN")
    @JsonIgnoreProperties({"tickets", "taches", "motDePasse", "departement"})
    private Utilisateur assigneAdmin;

    public Ticket() {}

    public Ticket(String titre, String description, String statut, String priorite,
                  Date dateCreation, Application application, Utilisateur createur) {
        this.titre = titre;
        this.description = description;
        this.statut = statut;
        this.priorite = priorite;
        this.dateCreation = dateCreation;
        this.application = application;
        this.createur = createur;
    }

    public Long getIdTicket() { return idTicket; }
    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public String getStatut() { return statut; }
    public String getPriorite() { return priorite; }
    public String getImagePath() { return imagePath; }
    public Date getDateCreation() { return dateCreation; }
    public Date getDateModification() { return dateModification; }
    public Date getDateResolution() { return dateResolution; }
    public Date getDateFermeture() { return dateFermeture; }
    public Application getApplication() { return application; }
    public Utilisateur getCreateur() { return createur; }
    public Utilisateur getAssigneAdmin() { return assigneAdmin; }

    public void setIdTicket(Long idTicket) { this.idTicket = idTicket; }
    public void setTitre(String titre) { this.titre = titre; }
    public void setDescription(String description) { this.description = description; }
    public void setStatut(String statut) { this.statut = statut; }
    public void setPriorite(String priorite) { this.priorite = priorite; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }
    public void setDateModification(Date dateModification) { this.dateModification = dateModification; }
    public void setDateResolution(Date dateResolution) { this.dateResolution = dateResolution; }
    public void setDateFermeture(Date dateFermeture) { this.dateFermeture = dateFermeture; }
    public void setApplication(Application application) { this.application = application; }
    public void setCreateur(Utilisateur createur) { this.createur = createur; }
    public void setAssigneAdmin(Utilisateur assigneAdmin) { this.assigneAdmin = assigneAdmin; }
}