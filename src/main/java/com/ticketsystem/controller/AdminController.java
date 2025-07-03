package com.ticketsystem.controller;

import com.ticketsystem.model.Ticket;
import com.ticketsystem.model.User;
import com.ticketsystem.repository.UserRepository;
import com.ticketsystem.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/tickets")
    public List<Map<String, Object>> getAllTicketsWithUsernames() {
        List<Ticket> tickets = ticketService.getAllTickets();
        Map<String, String> userIdToName = new HashMap<>();
        userRepository.findAll().forEach(user ->
            userIdToName.put(user.getId().toString(), user.getUsername())
        );

        List<Map<String, Object>> result = new ArrayList<>();
        for (Ticket t : tickets) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", t.getId());
            map.put("serviceType", t.getServiceType());
            map.put("serviceName", t.getServiceName());
            map.put("paymentStatus", t.getPaymentStatus());
            map.put("quantity", t.getQuantity());
            map.put("totalPrice", t.getTotalPrice());
            map.put("bookingDate", t.getBookingDate());
            map.put("user", Collections.singletonMap("username",
                userIdToName.getOrDefault(t.getUserId(), "N/A")));
            result.add(map);
        }
        return result;
    }

    @PutMapping("/cancel/{ticketId}")
    public ResponseEntity<?> cancelTicket(@PathVariable Long ticketId) {
        return ticketService.cancelPendingTicket(ticketId);
    }

    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", ticketService.getTotalTicketsCount());
        stats.put("paid", ticketService.getPaidTicketsCount());
        stats.put("pending", ticketService.getPendingTicketsCount());
        stats.put("cancelled", ticketService.getCancelledTicketsCount());
        return stats;
    }
}
