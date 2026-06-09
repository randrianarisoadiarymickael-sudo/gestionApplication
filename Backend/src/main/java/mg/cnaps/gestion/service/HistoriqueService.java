package mg.cnaps.gestion.service;

import mg.cnaps.gestion.dto.HistoriqueDTO;
import mg.cnaps.gestion.entity.Historique;
import mg.cnaps.gestion.entity.Tache;
import mg.cnaps.gestion.entity.Ticket;
import mg.cnaps.gestion.entity.Utilisateur;
import mg.cnaps.gestion.repository.HistoriqueRepository;
import mg.cnaps.gestion.repository.TacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class HistoriqueService {

    @Autowired
    private HistoriqueRepository historiqueRepository;

    @Autowired
    private TacheRepository tacheRepository;

    @Transactional(readOnly = true)
    public List<Historique> findByTicket(Ticket ticket) {

        // 1. Historiques directement liés au ticket
        List<Historique> parTicket = historiqueRepository
            .findByTicketOrderByDateActionAsc(ticket);

        // 2. Historiques liés aux tâches du ticket
        List<Tache> taches = tacheRepository.findByTicket(ticket);
        List<Historique> parTache = new ArrayList<>();
        for (Tache t : taches) {
            parTache.addAll(
                historiqueRepository.findByTacheOrderByDateActionDesc(t)
            );
        }

        // 3. Fusionner sans doublons
        List<Historique> fusion = new ArrayList<>(parTicket);
        for (Historique h : parTache) {
            boolean dejaDedans = fusion.stream()
                .anyMatch(e -> e.getIdHistorique().equals(h.getIdHistorique()));
            if (!dejaDedans) {
                fusion.add(h);
            }
        }

        // 4. Plus récent en haut
        fusion.sort(Comparator.comparing(Historique::getDateAction).reversed());

        return fusion;
    }

    // Récupérer l'historique d'une tâche
    public List<Historique> findByTache(Long idTache) {
        Tache tache = new Tache();
        tache.setIdTache(idTache);
        return historiqueRepository.findByTacheOrderByDateActionDesc(tache);
    }

    // Enregistrer une action liée à un ticket
    public Historique enregistrerAction(String typeAction, String descriptionAction,
                                        String ancienneValeur, String nouvelleValeur,
                                        Ticket ticket, Utilisateur utilisateur, Tache tache) {
        Historique historique = new Historique();
        historique.setTypeAction(typeAction);
        historique.setDescriptionAction(descriptionAction);
        historique.setAncienneValeur(ancienneValeur);
        historique.setNouvelleValeur(nouvelleValeur);
        historique.setDateAction(new Date());
        historique.setTicket(ticket);
        historique.setUtilisateur(utilisateur);
        historique.setTache(tache);
        return historiqueRepository.save(historique);
    }

    // Enregistrer une action liée à une tâche
    public Historique enregistrerActionTache(String typeAction, String descriptionAction,
                                              String ancienStatut, String nouveauStatut,
                                              Long idTache, Long idDeveloppeur) {
        Historique historique = new Historique();
        historique.setTypeAction(typeAction);
        historique.setDescriptionAction(descriptionAction);
        historique.setAncienneValeur(ancienStatut);
        historique.setNouvelleValeur(nouveauStatut);
        historique.setDateAction(new Date());

        Tache tache = new Tache();
        tache.setIdTache(idTache);
        historique.setTache(tache);

        // Stocker le ticket pour que l'admin le voie dans l'historique
        Optional<Tache> tacheComplete = tacheRepository.findById(idTache);
        if (tacheComplete.isPresent() && tacheComplete.get().getTicket() != null) {
            historique.setTicket(tacheComplete.get().getTicket());
        }

        if (idDeveloppeur != null) {
            Utilisateur dev = new Utilisateur();
            dev.setIdUtilisateur(idDeveloppeur);
            historique.setUtilisateur(dev);
        }

        return historiqueRepository.save(historique);
    }

    // Convertir un Historique en DTO
    public HistoriqueDTO toDTO(Historique h) {
        HistoriqueDTO dto = new HistoriqueDTO();
        dto.setIdHistorique(h.getIdHistorique());
        dto.setTypeAction(h.getTypeAction());
        dto.setDescriptionAction(h.getDescriptionAction());
        dto.setAncienneValeur(h.getAncienneValeur());
        dto.setNouvelleValeur(h.getNouvelleValeur());
        dto.setDateAction(h.getDateAction());

        if (h.getUtilisateur() != null) {
            dto.setUtilisateurNom(h.getUtilisateur().getNom());
            dto.setUtilisateurPrenom(h.getUtilisateur().getPrenom());
            dto.setUtilisateurId(h.getUtilisateur().getIdUtilisateur());
        }

        return dto;
    }
}