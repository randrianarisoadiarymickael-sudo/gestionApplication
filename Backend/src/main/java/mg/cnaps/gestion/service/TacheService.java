package mg.cnaps.gestion.service;

import mg.cnaps.gestion.entity.Tache;
import mg.cnaps.gestion.entity.Ticket;
import mg.cnaps.gestion.entity.Utilisateur;
import mg.cnaps.gestion.repository.TacheRepository;
import mg.cnaps.gestion.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TacheService {

    @Autowired
    private TacheRepository tacheRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private HistoriqueService historiqueService;

    public List<Tache> findAll() {
        return tacheRepository.findAll();
    }

    public Optional<Tache> findById(Long id) {
        return tacheRepository.findById(id);
    }

    public List<Tache> findByTicket(Ticket ticket) {
        return tacheRepository.findByTicket(ticket);
    }

    public List<Tache> findByDeveloppeur(Utilisateur developpeur) {
        return tacheRepository.findByDeveloppeur(developpeur);
    }

    @Transactional
    public Tache save(Tache tache) {
        tache.setDateCreation(new Date());
        tache.setStatut("EN_ATTENTE");
        Tache saved = tacheRepository.save(tache);

        try {
            Long idDev = saved.getDeveloppeur() != null ?
                saved.getDeveloppeur().getIdUtilisateur() : null;
            historiqueService.enregistrerActionTache(
                "ASSIGNATION",
                "Tâche assignée au développeur",
                null,
                "EN_ATTENTE",
                saved.getIdTache(),
                idDev
            );
        } catch (Exception e) {
            System.out.println("Erreur historique assignation: " + e.getMessage());
        }

        return saved;
    }

    @Transactional
    public Tache changerStatut(Long id, String nouveauStatut) {
        Optional<Tache> tacheOpt = tacheRepository.findById(id);
        if (tacheOpt.isPresent()) {
            Tache tache = tacheOpt.get();
            String ancienStatut = tache.getStatut();
            tache.setStatut(nouveauStatut);
            if (nouveauStatut.equals("TERMINEE")) {
                tache.setDateFinReelle(new Date());
            }
            Tache saved = tacheRepository.save(tache);

            try {
                Long idDev = tache.getDeveloppeur() != null ?
                    tache.getDeveloppeur().getIdUtilisateur() : null;
                historiqueService.enregistrerActionTache(
                    "CHANGEMENT_STATUT",
                    "Changement de statut de la tâche",
                    ancienStatut,
                    nouveauStatut,
                    id,
                    idDev
                );
            } catch (Exception e) {
                System.out.println("Erreur historique statut: " + e.getMessage());
            }

            try {
                synchroniserTicket(tache.getTicket(), nouveauStatut);
            } catch (Exception e) {
                System.out.println("Erreur sync ticket: " + e.getMessage());
            }

            return saved;
        }
        return null;
    }

    @Transactional
    public Tache changerStatutAvecCommentaire(Long id, String nouveauStatut,
                                               String commentaire, Long idDeveloppeur) {
        Optional<Tache> tacheOpt = tacheRepository.findById(id);
        if (tacheOpt.isPresent()) {
            Tache tache = tacheOpt.get();
            String ancienStatut = tache.getStatut();
            tache.setStatut(nouveauStatut);
            if (nouveauStatut.equals("TERMINEE")) {
                tache.setDateFinReelle(new Date());
            }
            Tache saved = tacheRepository.save(tache);

            try {
                historiqueService.enregistrerActionTache(
                    "CHANGEMENT_STATUT",
                    commentaire != null && !commentaire.isEmpty() ?
                        commentaire : "Changement de statut de la tâche",
                    ancienStatut,
                    nouveauStatut,
                    id,
                    idDeveloppeur
                );
            } catch (Exception e) {
                System.out.println("Erreur historique avec commentaire: " + e.getMessage());
            }

            try {
                synchroniserTicket(tache.getTicket(), nouveauStatut);
            } catch (Exception e) {
                System.out.println("Erreur sync ticket: " + e.getMessage());
            }

            return saved;
        }
        return null;
    }

    @Transactional
    public Tache terminerTache(Long id) {
        Optional<Tache> tacheOpt = tacheRepository.findById(id);
        if (tacheOpt.isPresent()) {
            Tache tache = tacheOpt.get();
            String ancienStatut = tache.getStatut();
            tache.setStatut("TERMINEE");
            tache.setDateFinReelle(new Date());
            Tache saved = tacheRepository.save(tache);

            try {
                Long idDev = tache.getDeveloppeur() != null ?
                    tache.getDeveloppeur().getIdUtilisateur() : null;
                historiqueService.enregistrerActionTache(
                    "TERMINAISON",
                    "Tâche marquée comme terminée",
                    ancienStatut,
                    "TERMINEE",
                    id,
                    idDev
                );
            } catch (Exception e) {
                System.out.println("Erreur historique terminaison: " + e.getMessage());
            }

            try {
                synchroniserTicket(tache.getTicket(), "TERMINEE");
            } catch (Exception e) {
                System.out.println("Erreur sync ticket: " + e.getMessage());
            }

            return saved;
        }
        return null;
    }

    @Transactional
    public void ajouterRapport(Long idTache, String contenuRapport, Long idDeveloppeur) {
        Optional<Tache> tacheOpt = tacheRepository.findById(idTache);
        if (tacheOpt.isPresent()) {
            historiqueService.enregistrerActionTache(
                "RAPPORT",
                contenuRapport,
                null,
                null,
                idTache,
                idDeveloppeur
            );
        }
    }

    @Transactional
    public Tache reassigner(Long idTache, Long idNouveauDeveloppeur) {
        Optional<Tache> tacheOpt = tacheRepository.findById(idTache);
        if (tacheOpt.isPresent()) {
            Tache tache = tacheOpt.get();

            // Garder l'ancien dev pour l'historique ET pour la visibilité lecture seule
            Utilisateur ancienDev = tache.getDeveloppeur();
            String ancienDevNom = ancienDev != null
                ? ancienDev.getNom() + " " + ancienDev.getPrenom()
                : "Aucun";

            // Sauvegarder l'ancien dev dans le champ dédié
            tache.setAncienDeveloppeur(ancienDev);

            // Affecter le nouveau développeur
            Utilisateur nouveauDev = new Utilisateur();
            nouveauDev.setIdUtilisateur(idNouveauDeveloppeur);
            tache.setDeveloppeur(nouveauDev);

            // Remettre la tâche en EN_ATTENTE
            tache.setStatut("EN_ATTENTE");
            tache.setDateFinReelle(null);

            Tache saved = tacheRepository.save(tache);

            try {
                historiqueService.enregistrerActionTache(
                    "ASSIGNATION",
                    "Tâche réassignée — ancien développeur : " + ancienDevNom,
                    ancienDevNom,
                    "Dev #" + idNouveauDeveloppeur,
                    idTache,
                    idNouveauDeveloppeur
                );
            } catch (Exception e) {
                System.out.println("Erreur historique réassignation: " + e.getMessage());
            }

            // Remettre le ticket en EN_COURS si RESOLU
            try {
                if (tache.getTicket() != null) {
                    Optional<Ticket> ticketOpt = ticketRepository
                        .findById(tache.getTicket().getIdTicket());
                    if (ticketOpt.isPresent()) {
                        Ticket t = ticketOpt.get();
                        if ("RESOLU".equals(t.getStatut())) {
                            t.setStatut("EN_COURS");
                            t.setDateModification(new Date());
                            ticketRepository.save(t);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Erreur sync ticket réassignation: " + e.getMessage());
            }

            return saved;
        }
        return null;
    }

    // Tâches réassignées — visibles en lecture seule par l'ancien dev
    public Page<Tache> findByAncienDeveloppeurPagine(Long idAncienDev, int page, int size) {
        Utilisateur dev = new Utilisateur();
        dev.setIdUtilisateur(idAncienDev);
        Pageable pageable = PageRequest.of(page, size, Sort.by("idTache").descending());
        return tacheRepository.findByAncienDeveloppeur(dev, pageable);
    }

    private void synchroniserTicket(Ticket ticket, String statutTache) {
        if (ticket == null || ticket.getIdTicket() == null) return;
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticket.getIdTicket());
        if (ticketOpt.isPresent()) {
            Ticket t = ticketOpt.get();
            switch (statutTache) {
                case "EN_COURS":
                    t.setStatut("EN_COURS");
                    break;
                case "EN_TEST":
                    t.setStatut("EN_TEST");
                    break;
                case "TERMINEE":
                    t.setStatut("RESOLU");
                    t.setDateResolution(new Date());
                    break;
                default:
                    break;
            }
            t.setDateModification(new Date());
            ticketRepository.save(t);
        }
    }

    public void deleteById(Long id) {
        tacheRepository.deleteById(id);
    }

    public Page<Tache> findByDeveloppeurPagine(Long idDeveloppeur, int page, int size) {
        Utilisateur dev = new Utilisateur();
        dev.setIdUtilisateur(idDeveloppeur);
        Pageable pageable = PageRequest.of(page, size, Sort.by("idTache").descending());
        return tacheRepository.findByDeveloppeur(dev, pageable);
    }
}