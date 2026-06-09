package mg.cnaps.gestion.service;

import mg.cnaps.gestion.entity.Departement;
import mg.cnaps.gestion.repository.DepartementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartementService {

    @Autowired
    private DepartementRepository departementRepository;

    // Récupérer tous les départements
    public List<Departement> findAll() {
        return departementRepository.findAll();
    }

    // Récupérer un département par ID
    public Optional<Departement> findById(Long id) {
        return departementRepository.findById(id);
    }

    // Créer ou modifier un département
    public Departement save(Departement departement) {
        return departementRepository.save(departement);
    }

    // Supprimer un département
    public void deleteById(Long id) {
        departementRepository.deleteById(id);
    }
}