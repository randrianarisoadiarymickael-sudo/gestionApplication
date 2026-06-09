package mg.cnaps.gestion.dto;

public class LoginResponse {
    private boolean success;
    private String message;
    private String role;
    private Long idUtilisateur;
    private String nom;
    private String prenom;
    private String matricule;

    public LoginResponse() {}

    public LoginResponse(boolean success, String message, String role,
                         Long idUtilisateur, String nom, String prenom, String matricule) {
        this.success = success;
        this.message = message;
        this.role = role;
        this.idUtilisateur = idUtilisateur;
        this.nom = nom;
        this.prenom = prenom;
        this.matricule = matricule;
    }

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getRole() { return role; }
    public Long getIdUtilisateur() { return idUtilisateur; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getMatricule() { return matricule; }

    // Setters
    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String message) { this.message = message; }
    public void setRole(String role) { this.role = role; }
    public void setIdUtilisateur(Long idUtilisateur) { this.idUtilisateur = idUtilisateur; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
}