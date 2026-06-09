package mg.cnaps.gestion.service;

import mg.cnaps.gestion.entity.Application;
import mg.cnaps.gestion.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    // Récupérer toutes les applications
    public List<Application> findAll() {
        return applicationRepository.findAll();
    }

    // Récupérer une application par ID
    public Optional<Application> findById(Long id) {
        return applicationRepository.findById(id);
    }

    // Créer ou modifier une application
    public Application save(Application application) {
        return applicationRepository.save(application);
    }

    // Supprimer une application
    public void deleteById(Long id) {
        applicationRepository.deleteById(id);
    }
}