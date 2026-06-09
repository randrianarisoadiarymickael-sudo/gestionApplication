package mg.cnaps.gestion.repository;

import mg.cnaps.gestion.entity.Ticket;
import mg.cnaps.gestion.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCreateur(Utilisateur createur);
    List<Ticket> findByStatut(String statut);
    List<Ticket> findByAssigneAdmin(Utilisateur assigneAdmin);
    Page<Ticket> findAll(Pageable pageable);
    long countByStatut(String statut);   // ← ajouté
}