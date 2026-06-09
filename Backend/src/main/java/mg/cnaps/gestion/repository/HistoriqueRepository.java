package mg.cnaps.gestion.repository;

import mg.cnaps.gestion.entity.Historique;
import mg.cnaps.gestion.entity.Ticket;
import mg.cnaps.gestion.entity.Tache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueRepository extends JpaRepository<Historique, Long> {
    List<Historique> findByTicket(Ticket ticket);
    List<Historique> findByTicketOrderByDateActionAsc(Ticket ticket);
    List<Historique> findByTacheOrderByDateActionDesc(Tache tache);
}