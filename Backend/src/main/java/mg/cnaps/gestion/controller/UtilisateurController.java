package mg.cnaps.gestion.controller;

import mg.cnaps.gestion.entity.Utilisateur;
import mg.cnaps.gestion.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin(origins = "*")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    // GET tous les utilisateurs
    @GetMapping
    public List<Utilisateur> findAll() {
        return utilisateurService.findAll();
    }

    // GET tous les développeurs
    @GetMapping("/developpeurs")
    public List<Utilisateur> getDeveloppeurs() {
    return utilisateurService.findByRole("DEV");
    }

    // GET un utilisateur par ID
    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> findById(@PathVariable Long id) {
        Optional<Utilisateur> utilisateur = utilisateurService.findById(id);
        if (utilisateur.isPresent()) {
            return ResponseEntity.ok(utilisateur.get());
        }
        return ResponseEntity.notFound().build();
    }

    // POST créer un utilisateur
    @PostMapping
    public Utilisateur save(@RequestBody Utilisateur utilisateur) {
        return utilisateurService.save(utilisateur);
    }

    // PUT modifier un utilisateur
    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> update(@PathVariable Long id,
                                               @RequestBody Utilisateur utilisateur) {
        Optional<Utilisateur> existing = utilisateurService.findById(id);
        if (existing.isPresent()) {
            utilisateur.setIdUtilisateur(id);
            return ResponseEntity.ok(utilisateurService.save(utilisateur));
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE supprimer un utilisateur
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        utilisateurService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}