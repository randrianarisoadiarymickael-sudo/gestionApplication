package mg.cnaps.gestion.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "UTILISATEUR")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_util")
    @SequenceGenerator(name = "seq_util", sequenceName = "SEQ_UTILISATEUR", allocationSize = 1)
    @Column(name = "ID_UTILISATEUR")
    private Long idUtilisateur;

    @Column(name = "MATRICULE", nullable = false, unique = true, length = 20)
    private String matricule;

    @Column(name = "NOM", nullable = false, length = 100)
    private String nom;

    @Column(name = "PRENOM", nullable = false, length = 100)
    private String prenom;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "MOT_DE_PASSE", nullable = false, length = 255)
    private String motDePasse;

    @Column(name = "ROLE", nullable = false, length = 20)
    private String role;

    @Column(name = "DATE_CREATION", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateCreation;

    @Column(name = "ACTIF", nullable = false)
    private Integer actif = 1;

    @ManyToOne
    @JoinColumn(name = "ID_DEPARTEMENT")
    @JsonIgnoreProperties({"utilisateurs", "employes"})  // ← coupe la boucle Departement→Utilisateur
    private Departement departement;

    public Utilisateur() {}

    public Utilisateur(String matricule, String nom, String prenom, String email,
                       String motDePasse, String role, Date dateCreation, Departement departement) {
        this.matricule = matricule;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.dateCreation = dateCreation;
        this.departement = departement;
        this.actif = 1;
    }

    public Long getIdUtilisateur() { return idUtilisateur; }
    public String getMatricule() { return matricule; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public String getMotDePasse() { return motDePasse; }
    public String getRole() { return role; }
    public Date getDateCreation() { return dateCreation; }
    public Integer getActif() { return actif; }
    public Departement getDepartement() { return departement; }

    public void setIdUtilisateur(Long idUtilisateur) { this.idUtilisateur = idUtilisateur; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setEmail(String email) { this.email = email; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public void setRole(String role) { this.role = role; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }
    public void setActif(Integer actif) { this.actif = actif; }
    public void setDepartement(Departement departement) { this.departement = departement; }
}