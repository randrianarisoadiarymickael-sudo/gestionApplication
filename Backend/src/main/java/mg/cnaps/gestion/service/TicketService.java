package mg.cnaps.gestion.service;

import mg.cnaps.gestion.entity.Historique;
import mg.cnaps.gestion.entity.Tache;
import mg.cnaps.gestion.entity.Ticket;
import mg.cnaps.gestion.entity.Utilisateur;
import mg.cnaps.gestion.repository.HistoriqueRepository;
import mg.cnaps.gestion.repository.TacheRepository;
import mg.cnaps.gestion.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private HistoriqueRepository historiqueRepository;

    @Autowired
    private TacheRepository tacheRepository;

    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    public Page<Ticket> findAllPagine(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("idTicket").descending());
        return ticketRepository.findAll(pageable);
    }

    public Optional<Ticket> findById(Long id) {
        return ticketRepository.findById(id);
    }

    public List<Ticket> findByCreateur(Utilisateur createur) {
        return ticketRepository.findByCreateur(createur);
    }

    public List<Ticket> findByStatut(String statut) {
        return ticketRepository.findByStatut(statut);
    }

    // ← ajouté
    public Map<String, Long> countParStatut() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("NOUVEAU",  ticketRepository.countByStatut("NOUVEAU"));
        stats.put("EN_COURS", ticketRepository.countByStatut("EN_COURS"));
        stats.put("EN_TEST",  ticketRepository.countByStatut("EN_TEST"));
        stats.put("RESOLU",   ticketRepository.countByStatut("RESOLU"));
        return stats;
    }

    @Transactional
    public Ticket save(Ticket ticket) {
        ticket.setDateCreation(new Date());
        ticket.setStatut("NOUVEAU");
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket changerStatut(Long id, String nouveauStatut) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(id);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            String ancienStatut = ticket.getStatut();

            ticket.setStatut(nouveauStatut);
            ticket.setDateModification(new Date());

            if (nouveauStatut.equals("RESOLU")) {
                ticket.setDateResolution(new Date());
            }
            if (nouveauStatut.equals("FERME")) {
                ticket.setDateFermeture(new Date());
            }

            Ticket ticketSauvegarde = ticketRepository.save(ticket);
            ticketRepository.flush();
            synchroniserTaches(ticketSauvegarde, nouveauStatut);

            try {
                Historique historique = new Historique();
                historique.setTypeAction("CHANGEMENT_STATUT");
                historique.setDescriptionAction("Changement de statut : " + ancienStatut + " → " + nouveauStatut);
                historique.setAncienneValeur(ancienStatut);
                historique.setNouvelleValeur(nouveauStatut);
                historique.setDateAction(new Date());
                historique.setTicket(ticketSauvegarde);
                if (ticketSauvegarde.getCreateur() != null) {
                    historique.setUtilisateur(ticketSauvegarde.getCreateur());
                }
                historiqueRepository.save(historique);
                historiqueRepository.flush();
            } catch (Exception e) {
                System.out.println("Erreur historique statut: " + e.getMessage());
            }

            return ticketSauvegarde;
        }
        return null;
    }

    private void synchroniserTaches(Ticket ticket, String statutTicket) {
        try {
            List<Tache> taches = tacheRepository.findByTicket(ticket);
            for (Tache tache : taches) {
                String nouveauStatutTache;
                switch (statutTicket) {
                    case "EN_COURS":
                        nouveauStatutTache = "EN_COURS";
                        break;
                    case "EN_TEST":
                        nouveauStatutTache = "EN_TEST";
                        break;
                    case "RESOLU":
                        nouveauStatutTache = "TERMINEE";
                        tache.setDateFinReelle(new Date());
                        break;
                    case "NOUVEAU":
                        nouveauStatutTache = "EN_ATTENTE";
                        break;
                    default:
                        nouveauStatutTache = tache.getStatut();
                        break;
                }
                tache.setStatut(nouveauStatutTache);
                tacheRepository.save(tache);
            }
        } catch (Exception e) {
            System.out.println("Erreur synchronisation taches: " + e.getMessage());
        }
    }

    public void deleteById(Long id) {
        ticketRepository.deleteById(id);
    }
}