package mg.cnaps.gestion.controller;

import mg.cnaps.gestion.entity.Application;
import mg.cnaps.gestion.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    // GET tous les applications
    @GetMapping
    public ResponseEntity<List<Application>> findAll() {
        return ResponseEntity.ok(applicationService.findAll());
    }

    // GET application par ID
    @GetMapping("/{id}")
    public ResponseEntity<Application> findById(@PathVariable Long id) {
        Optional<Application> application = applicationService.findById(id);
        if (application.isPresent()) {
            return ResponseEntity.ok(application.get());
        }
        return ResponseEntity.notFound().build();
    }

    // POST créer une application
    @PostMapping
    public ResponseEntity<Application> save(@RequestBody Application application) {
        return ResponseEntity.ok(applicationService.save(application));
    }

    // PUT modifier une application
    @PutMapping("/{id}")
    public ResponseEntity<Application> update(@PathVariable Long id, @RequestBody Application application) {
        application.setIdApplication(id);
        return ResponseEntity.ok(applicationService.save(application));
    }

    // DELETE supprimer une application
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        applicationService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // PUT renommer une application (ADMIN/DEV)
    @PutMapping("/{id}/nom")
    public ResponseEntity<String> renommerApplication(
        @PathVariable Long id,
        @RequestBody Map<String, String> body) {
    try {
        Optional<Application> opt = applicationService.findById(id);
        if (!opt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        String nouveauNom = body.get("nomApplication");
        if (nouveauNom == null || nouveauNom.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Nom invalide");
        }
        Application app = opt.get();
        app.setNomApplication(nouveauNom.trim());
        applicationService.save(app);
        System.out.println("Application " + id + " renommée en : " + nouveauNom);
        return ResponseEntity.ok("Application renommée avec succès");
    } catch (Exception e) {
        System.out.println("Erreur renommage application : " + e.getMessage());
        return ResponseEntity.internalServerError().body("Erreur serveur");
    }
}
}