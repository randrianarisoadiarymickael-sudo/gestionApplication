package mg.cnaps.gestion.controller;

import mg.cnaps.gestion.dto.HistoriqueDTO;
import mg.cnaps.gestion.entity.Historique;
import mg.cnaps.gestion.entity.Ticket;
import mg.cnaps.gestion.service.HistoriqueService;
import mg.cnaps.gestion.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/historiques")
@CrossOrigin(origins = "*")
public class HistoriqueController {

    @Autowired
    private HistoriqueService historiqueService;

    @Autowired
    private TicketService ticketService;

    // GET historique d'un ticket — retourne des DTO pour éviter les références circulaires
    @GetMapping("/ticket/{idTicket}")
    public ResponseEntity<List<HistoriqueDTO>> findByTicket(@PathVariable Long idTicket) {
        Optional<Ticket> ticket = ticketService.findById(idTicket);
        if (ticket.isPresent()) {
            List<HistoriqueDTO> dtos = historiqueService
                .findByTicket(ticket.get())
                .stream()
                .map(h -> historiqueService.toDTO(h))
                .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        }
        return ResponseEntity.notFound().build();
    }
}