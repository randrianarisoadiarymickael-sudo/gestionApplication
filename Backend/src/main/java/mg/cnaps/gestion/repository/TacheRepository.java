package mg.cnaps.gestion.repository;

import mg.cnaps.gestion.entity.Tache;
import mg.cnaps.gestion.entity.Ticket;
import mg.cnaps.gestion.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface TacheRepository extends JpaRepository<Tache, Long> {
    List<Tache> findByTicket(Ticket ticket);
    List<Tache> findByDeveloppeur(Utilisateur developpeur);
    List<Tache> findByStatut(String statut);
    Page<Tache> findByDeveloppeur(Utilisateur developpeur, Pageable pageable);

    // Tâches réassignées — visibles en lecture seule par l'ancien dev
    Page<Tache> findByAncienDeveloppeur(Utilisateur ancienDeveloppeur, Pageable pageable);
}