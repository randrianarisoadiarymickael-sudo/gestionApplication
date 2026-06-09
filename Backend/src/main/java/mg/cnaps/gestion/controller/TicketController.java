package mg.cnaps.gestion.controller;

import mg.cnaps.gestion.entity.Ticket;
import mg.cnaps.gestion.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping
    public List<Ticket> findAll() {
        return ticketService.findAll();
    }

    @GetMapping("/pagine")
    public ResponseEntity<Page<Ticket>> getAllPagine(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ticketService.findAllPagine(page, size));
    }

    @GetMapping("/stats-statut")                         // ← ajouté
    public ResponseEntity<Map<String, Long>> getStatsParStatut() {
        return ResponseEntity.ok(ticketService.countParStatut());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> findById(@PathVariable Long id) {
        Optional<Ticket> ticket = ticketService.findById(id);
        if (ticket.isPresent()) {
            return ResponseEntity.ok(ticket.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/statut/{statut}")
    public List<Ticket> findByStatut(@PathVariable String statut) {
        return ticketService.findByStatut(statut);
    }

    @PostMapping
    public Ticket save(@RequestBody Ticket ticket) {
        return ticketService.save(ticket);
    }

    @PutMapping("/{id}/statut/{statut}")
    public ResponseEntity<Ticket> changerStatut(@PathVariable Long id,
                                                 @PathVariable String statut) {
        Ticket ticket = ticketService.changerStatut(id, statut);
        if (ticket != null) {
            return ResponseEntity.ok(ticket);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> update(@PathVariable Long id,
                                          @RequestBody Ticket ticket) {
        Optional<Ticket> existing = ticketService.findById(id);
        if (existing.isPresent()) {
            ticket.setIdTicket(id);
            return ResponseEntity.ok(ticketService.save(ticket));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ticketService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}