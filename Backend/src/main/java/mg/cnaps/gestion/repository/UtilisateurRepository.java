package mg.cnaps.gestion.repository;

import mg.cnaps.gestion.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Utilisateur findByEmail(String email);
    Utilisateur findByMatricule(String matricule);
    Utilisateur findByMatriculeAndMotDePasse(String matricule, String motDePasse);
    List<Utilisateur> findByRole(String role);
}