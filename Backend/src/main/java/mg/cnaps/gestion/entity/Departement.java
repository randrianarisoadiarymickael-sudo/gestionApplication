package mg.cnaps.gestion.entity;

import javax.persistence.*;

@Entity
@Table(name = "DEPARTEMENT")
public class Departement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_dept")
    @SequenceGenerator(name = "seq_dept", sequenceName = "SEQ_DEPARTEMENT", allocationSize = 1)
    @Column(name = "ID_DEPARTEMENT")
    private Long idDepartement;

    @Column(name = "NOM_DEPARTEMENT", nullable = false, length = 100)
    private String nomDepartement;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    // Constructeurs
    public Departement() {}

    public Departement(String nomDepartement, String description) {
        this.nomDepartement = nomDepartement;
        this.description = description;
    }

    // Getters
    public Long getIdDepartement() { return idDepartement; }
    public String getNomDepartement() { return nomDepartement; }
    public String getDescription() { return description; }

    // Setters
    public void setIdDepartement(Long idDepartement) { this.idDepartement = idDepartement; }
    public void setNomDepartement(String nomDepartement) { this.nomDepartement = nomDepartement; }
    public void setDescription(String description) { this.description = description; }
}