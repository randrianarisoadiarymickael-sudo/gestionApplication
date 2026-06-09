package mg.cnaps.gestion.controller;

import mg.cnaps.gestion.dto.LoginRequest;
import mg.cnaps.gestion.dto.LoginResponse;
import mg.cnaps.gestion.entity.Utilisateur;
import mg.cnaps.gestion.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UtilisateurService utilisateurService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        Utilisateur utilisateur = utilisateurService.login(
            loginRequest.getMatricule(),
            loginRequest.getMotDePasse()
        );

        if (utilisateur != null && utilisateur.getActif() == 1) {
            LoginResponse response = new LoginResponse(
                true,
                "Connexion réussie",
                utilisateur.getRole(),
                utilisateur.getIdUtilisateur(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getMatricule()
            );
            return ResponseEntity.ok(response);
        }

        LoginResponse response = new LoginResponse(
            false,
            "Matricule ou mot de passe incorrect",
            null, null, null, null, null
        );
        return ResponseEntity.status(401).body(response);
    }
}