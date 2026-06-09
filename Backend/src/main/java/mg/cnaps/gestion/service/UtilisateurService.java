package mg.cnaps.gestion.service;

import mg.cnaps.gestion.entity.Utilisateur;
import mg.cnaps.gestion.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // Récupérer tous les utilisateurs
    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }

    public List<Utilisateur> findByRole(String role) {
    return utilisateurRepository.findByRole(role);
    }

    // Récupérer un utilisateur par ID
    public Optional<Utilisateur> findById(Long id) {
        return utilisateurRepository.findById(id);
    }

    // Récupérer un utilisateur par email
    public Utilisateur findByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    // Récupérer un utilisateur par matricule
    public Utilisateur findByMatricule(String matricule) {
        return utilisateurRepository.findByMatricule(matricule);
    }

    // Créer ou modifier un utilisateur
    public Utilisateur save(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    // Supprimer un utilisateur
    public void deleteById(Long id) {
        utilisateurRepository.deleteById(id);
    }
    
    // Authentification par matricule et mot de passe
    public Utilisateur login(String matricule, String motDePasse) {
    return utilisateurRepository.findByMatriculeAndMotDePasse(matricule, motDePasse);
    }
}