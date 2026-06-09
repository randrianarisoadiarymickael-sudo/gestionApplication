package mg.cnaps.gestion.dto;

import java.util.Date;

public class HistoriqueDTO {

    private Long idHistorique;
    private String typeAction;
    private String descriptionAction;
    private String ancienneValeur;
    private String nouvelleValeur;
    private Date dateAction;

    // Utilisateur simplifié
    private String utilisateurNom;
    private String utilisateurPrenom;
    private Long utilisateurId;

    public HistoriqueDTO() {}

    // Getters
    public Long getIdHistorique() { return idHistorique; }
    public String getTypeAction() { return typeAction; }
    public String getDescriptionAction() { return descriptionAction; }
    public String getAncienneValeur() { return ancienneValeur; }
    public String getNouvelleValeur() { return nouvelleValeur; }
    public Date getDateAction() { return dateAction; }
    public String getUtilisateurNom() { return utilisateurNom; }
    public String getUtilisateurPrenom() { return utilisateurPrenom; }
    public Long getUtilisateurId() { return utilisateurId; }

    // Setters
    public void setIdHistorique(Long idHistorique) { this.idHistorique = idHistorique; }
    public void setTypeAction(String typeAction) { this.typeAction = typeAction; }
    public void setDescriptionAction(String descriptionAction) { this.descriptionAction = descriptionAction; }
    public void setAncienneValeur(String ancienneValeur) { this.ancienneValeur = ancienneValeur; }
    public void setNouvelleValeur(String nouvelleValeur) { this.nouvelleValeur = nouvelleValeur; }
    public void setDateAction(Date dateAction) { this.dateAction = dateAction; }
    public void setUtilisateurNom(String utilisateurNom) { this.utilisateurNom = utilisateurNom; }
    public void setUtilisateurPrenom(String utilisateurPrenom) { this.utilisateurPrenom = utilisateurPrenom; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }
}