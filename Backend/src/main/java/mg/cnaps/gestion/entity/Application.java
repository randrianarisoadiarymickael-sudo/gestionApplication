package mg.cnaps.gestion.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "APPLICATION")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_app")
    @SequenceGenerator(name = "seq_app", sequenceName = "SEQ_APPLICATION", allocationSize = 1)
    @Column(name = "ID_APPLICATION")
    private Long idApplication;

    @Column(name = "NOM_APPLICATION", nullable = false, unique = true, length = 100)
    private String nomApplication;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    @Column(name = "VERSION", length = 20)
    private String version;

    @Column(name = "STATUT", nullable = false, length = 20)
    private String statut;

    @Column(name = "DATE_MISE_EN_PROD")
    @Temporal(TemporalType.DATE)
    private Date dateMiseEnProd;

    // Constructeurs
    public Application() {}

    public Application(String nomApplication, String description, String version,
                       String statut, Date dateMiseEnProd) {
        this.nomApplication = nomApplication;
        this.description = description;
        this.version = version;
        this.statut = statut;
        this.dateMiseEnProd = dateMiseEnProd;
    }

    // Getters
    public Long getIdApplication() { return idApplication; }
    public String getNomApplication() { return nomApplication; }
    public String getDescription() { return description; }
    public String getVersion() { return version; }
    public String getStatut() { return statut; }
    public Date getDateMiseEnProd() { return dateMiseEnProd; }

    // Setters
    public void setIdApplication(Long idApplication) { this.idApplication = idApplication; }
    public void setNomApplication(String nomApplication) { this.nomApplication = nomApplication; }
    public void setDescription(String description) { this.description = description; }
    public void setVersion(String version) { this.version = version; }
    public void setStatut(String statut) { this.statut = statut; }
    public void setDateMiseEnProd(Date dateMiseEnProd) { this.dateMiseEnProd = dateMiseEnProd; }
}