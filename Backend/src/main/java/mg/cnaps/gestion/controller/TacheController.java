package mg.cnaps.gestion.controller;

import mg.cnaps.gestion.dto.HistoriqueDTO;
import mg.cnaps.gestion.entity.Ticket;
import mg.cnaps.gestion.entity.Utilisateur;
import mg.cnaps.gestion.entity.Tache;
import mg.cnaps.gestion.service.TacheService;
import mg.cnaps.gestion.service.HistoriqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/taches")
@CrossOrigin(origins = "*")
public class TacheController {

    @Autowired
    private TacheService tacheService;

    @Autowired
    private HistoriqueService historiqueService;

    // ── Vérifie que idDeveloppeur est bien le dev ACTUEL de la tâche ──
    private boolean estDevActuel(Tache tache, Long idDeveloppeur) {
        if (idDeveloppeur == null) return false;
        if (tache.getDeveloppeur() == null) return false;
        return idDeveloppeur.equals(tache.getDeveloppeur().getIdUtilisateur());
    }

    @GetMapping
    public List<Tache> findAll() {
        return tacheService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tache> findById(@PathVariable Long id) {
        Optional<Tache> tache = tacheService.findById(id);
        if (tache.isPresent()) {
            return ResponseEntity.ok(tache.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/developpeur/{idDeveloppeur}")
    public List<Tache> findByDeveloppeur(@PathVariable Long idDeveloppeur) {
        Utilisateur dev = new Utilisateur();
        dev.setIdUtilisateur(idDeveloppeur);
        return tacheService.findByDeveloppeur(dev);
    }

    @GetMapping("/ticket/{idTicket}")
    public List<Tache> findByTicket(@PathVariable Long idTicket) {
        Ticket ticket = new Ticket();
        ticket.setIdTicket(idTicket);
        return tacheService.findByTicket(ticket);
    }

    @PostMapping
    public Tache save(@RequestBody Tache tache) {
        return tacheService.save(tache);
    }

    // ── TERMINER — vérifie que c'est bien le dev actuel ──
    @PutMapping("/{id}/terminer")
    public ResponseEntity<?> terminer(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Object> body) {

        System.out.println("=== terminer appelé — idTache: " + id);

        Optional<Tache> tacheOpt = tacheService.findById(id);
        if (tacheOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Long idDeveloppeur = null;
        if (body != null && body.get("idDeveloppeur") != null) {
            idDeveloppeur = Long.valueOf(body.get("idDeveloppeur").toString());
        }

        Tache tache = tacheOpt.get();
        System.out.println("=== Dev actuel de la tâche: "
            + (tache.getDeveloppeur() != null ? tache.getDeveloppeur().getIdUtilisateur() : "null"));
        System.out.println("=== idDeveloppeur reçu: " + idDeveloppeur);

        if (!estDevActuel(tache, idDeveloppeur)) {
            System.out.println("=== ACCÈS REFUSÉ /terminer");
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Accès refusé : vous n'êtes plus le développeur assigné à cette tâche.");
        }

        System.out.println("=== ACCÈS AUTORISÉ /terminer");
        Tache updated = tacheService.terminerTache(id);
        return ResponseEntity.ok(updated);
    }

    // ── CHANGER STATUT — vérifie que c'est bien le dev actuel ──
    @PutMapping("/{id}/statut")
    public ResponseEntity<?> changerStatut(
            @PathVariable Long id,
            @RequestParam String statut,
            @RequestParam(required = false) Long idDeveloppeur) {

        System.out.println("=== changerStatut appelé — idTache: " + id
            + " | statut: " + statut
            + " | idDeveloppeur reçu: " + idDeveloppeur);

        Optional<Tache> tacheOpt = tacheService.findById(id);
        if (tacheOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tache tache = tacheOpt.get();
        System.out.println("=== Dev actuel de la tâche: "
            + (tache.getDeveloppeur() != null ? tache.getDeveloppeur().getIdUtilisateur() : "null"));

        if (!estDevActuel(tache, idDeveloppeur)) {
            System.out.println("=== ACCÈS REFUSÉ /statut");
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Accès refusé : vous n'êtes plus le développeur assigné à cette tâche.");
        }

        System.out.println("=== ACCÈS AUTORISÉ /statut");
        Tache updated = tacheService.changerStatut(id, statut);
        return ResponseEntity.ok(updated);
    }

    // ── CHANGER STATUT AVEC COMMENTAIRE — vérifie que c'est bien le dev actuel ──
    @PutMapping("/{id}/statut-commentaire")
    public ResponseEntity<?> changerStatutAvecCommentaire(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {

        String nouveauStatut = (String) body.get("statut");
        String commentaire = (String) body.get("commentaire");
        Long idDeveloppeur = body.get("idDeveloppeur") != null ?
            Long.valueOf(body.get("idDeveloppeur").toString()) : null;

        System.out.println("=== changerStatutAvecCommentaire — idTache: " + id
            + " | idDeveloppeur reçu: " + idDeveloppeur);

        Optional<Tache> tacheOpt = tacheService.findById(id);
        if (tacheOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tache tache = tacheOpt.get();
        System.out.println("=== Dev actuel de la tâche: "
            + (tache.getDeveloppeur() != null ? tache.getDeveloppeur().getIdUtilisateur() : "null"));

        if (!estDevActuel(tache, idDeveloppeur)) {
            System.out.println("=== ACCÈS REFUSÉ /statut-commentaire");
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Accès refusé : vous n'êtes plus le développeur assigné à cette tâche.");
        }

        System.out.println("=== ACCÈS AUTORISÉ /statut-commentaire");
        Tache updated = tacheService.changerStatutAvecCommentaire(
            id, nouveauStatut, commentaire, idDeveloppeur
        );
        return ResponseEntity.ok(updated);
    }

    // ── RAPPORT — l'ancien dev PEUT encore ajouter un rapport ──
    @PutMapping("/{id}/rapport")
    public ResponseEntity<?> ajouterRapport(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {

        String rapport = (String) body.get("rapport");
        if (rapport == null || rapport.trim().isEmpty()) {
            return ResponseEntity
                .badRequest()
                .body("Le rapport ne peut pas être vide.");
        }

        Long idDeveloppeur = body.get("idDeveloppeur") != null ?
            Long.valueOf(body.get("idDeveloppeur").toString()) : null;

        try {
            tacheService.ajouterRapport(id, rapport.trim(), idDeveloppeur);
            return ResponseEntity.ok("Rapport enregistré.");
        } catch (Exception e) {
            System.out.println("ERREUR /rapport : " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur : " + e.getMessage());
        }
    }

    @PutMapping("/{id}/reassigner")
    public ResponseEntity<?> reassigner(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {

        Long idDeveloppeur = body.get("idDeveloppeur") != null
            ? Long.valueOf(body.get("idDeveloppeur").toString()) : null;

        if (idDeveloppeur == null) {
            return ResponseEntity
                .badRequest()
                .body("idDeveloppeur requis.");
        }

        try {
            Tache tache = tacheService.reassigner(id, idDeveloppeur);
            if (tache != null) {
                return ResponseEntity.ok("Tâche réassignée.");
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.out.println("ERREUR /reassigner : " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur : " + e.getMessage());
        }
    }

    @GetMapping("/{id}/historique")
    public ResponseEntity<?> getHistorique(@PathVariable Long id) {
        try {
            List<HistoriqueDTO> dtos = historiqueService
                .findByTache(id)
                .stream()
                .map(h -> historiqueService.toDTO(h))
                .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            System.out.println("ERREUR /historique : " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur chargement historique : " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tache> update(@PathVariable Long id,
                                         @RequestBody Tache tache) {
        Optional<Tache> existing = tacheService.findById(id);
        if (existing.isPresent()) {
            tache.setIdTache(id);
            return ResponseEntity.ok(tacheService.save(tache));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tacheService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/developpeur/{idDeveloppeur}/pagine")
    public ResponseEntity<Page<Tache>> getByDeveloppeurPagine(
            @PathVariable Long idDeveloppeur,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            tacheService.findByDeveloppeurPagine(idDeveloppeur, page, size)
        );
    }

    @GetMapping("/ancien-developpeur/{id}/pagine")
    public ResponseEntity<?> getTachesAncienDev(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Tache> taches = tacheService.findByAncienDeveloppeurPagine(id, page, size);
            return ResponseEntity.ok(taches);
        } catch (Exception e) {
            System.out.println("Erreur récupération tâches ancien dev: " + e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur serveur");
        }
    }
}