package mg.cnaps.gestion.controller;

import mg.cnaps.gestion.entity.Departement;
import mg.cnaps.gestion.service.DepartementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/departements")
@CrossOrigin(origins = "*")
public class DepartementController {

    @Autowired
    private DepartementService departementService;

    // GET tous les départements
    @GetMapping
    public List<Departement> findAll() {
        return departementService.findAll();
    }

    // GET un département par ID
    @GetMapping("/{id}")
    public ResponseEntity<Departement> findById(@PathVariable Long id) {
        Optional<Departement> departement = departementService.findById(id);
        if (departement.isPresent()) {
            return ResponseEntity.ok(departement.get());
        }
        return ResponseEntity.notFound().build();
    }

    // POST créer un département
    @PostMapping
    public Departement save(@RequestBody Departement departement) {
        return departementService.save(departement);
    }

    // PUT modifier un département
    @PutMapping("/{id}")
    public ResponseEntity<Departement> update(@PathVariable Long id,
                                               @RequestBody Departement departement) {
        Optional<Departement> existing = departementService.findById(id);
        if (existing.isPresent()) {
            departement.setIdDepartement(id);
            return ResponseEntity.ok(departementService.save(departement));
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE supprimer un département
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        departementService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}