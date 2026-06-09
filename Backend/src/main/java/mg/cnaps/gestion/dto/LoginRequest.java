package mg.cnaps.gestion.dto;

public class LoginRequest {
    private String matricule;
    private String motDePasse;

    public LoginRequest() {}

    public String getMatricule() { return matricule; }
    public String getMotDePasse() { return motDePasse; }

    public void setMatricule(String matricule) { this.matricule = matricule; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
}