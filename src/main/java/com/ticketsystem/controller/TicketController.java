package com.ticketsystem.controller;

import com.razorpay.RazorpayException;
import com.ticketsystem.exception.BadRequestException;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.model.Ticket;
import com.ticketsystem.payload.TicketDetailsDto;
import com.ticketsystem.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping(value = "/book", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> bookTicket(@RequestBody Ticket ticket) {
        try {
            if (ticket.getServiceType() == null || ticket.getServiceId() == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Service type and service ID are required"));
            }

            Ticket savedTicket = ticketService.bookTicket(ticket);
            return ResponseEntity.ok(savedTicket);
        } catch (ResourceNotFoundException | BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error booking ticket: " + e.getMessage()));
        }
    }

    @PostMapping(value = "/payment", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> paymentRequest) {
        try {
            Ticket ticket = new Ticket();
            ticket.setId(Long.parseLong(paymentRequest.get("id").toString()));
            ticket.setTotalPrice(Double.parseDouble(paymentRequest.get("totalPrice").toString()));

            String orderId = ticketService.createPaymentOrder(ticket);
            return ResponseEntity.ok().body(Map.of("orderId", orderId));
        } catch (RazorpayException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Payment Error: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error creating payment order: " + e.getMessage()));
        }
    }

    @PostMapping(value = "/updatePaymentStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePaymentStatus(@RequestBody Map<String, String> payload) {
        try {
            String ticketId = payload.get("ticketId");
            String paymentId = payload.get("paymentId");

            if (ticketId == null || paymentId == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Ticket ID and Payment ID are required"));
            }

            ticketService.updateTicketPaymentStatus(Long.parseLong(ticketId), paymentId, "PAID");
            return ResponseEntity.ok().body(Map.of("message", "Payment status updated successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error updating payment status: " + e.getMessage()));
        }
    }

    // Optional legacy endpoint if needed elsewhere
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Ticket>> getTicketsByUser(@PathVariable String userId) {
        try {
            List<Ticket> tickets = ticketService.getTicketsByUserId(userId);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // ✅ Recommended endpoint used by authenticated frontend
    @GetMapping("/my")
    public ResponseEntity<List<TicketDetailsDto>> getMyTickets() {
        try {
            String userId = ticketService.getCurrentUserId();
            List<TicketDetailsDto> tickets = ticketService.getTicketDTOsByUserId(userId);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}





//package com.ticketsystem.controller;
//
//import com.razorpay.RazorpayException;
//import com.ticketsystem.exception.BadRequestException;
//import com.ticketsystem.exception.ResourceNotFoundException;
//import com.ticketsystem.model.Ticket;
//import com.ticketsystem.service.TicketService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.*;
//
//@RestController
//@RequestMapping("/api/tickets")
//public class TicketController {
//
//    @Autowired
//    private TicketService ticketService;
//
//    @PostMapping(value = "/book", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> bookTicket(@RequestBody Ticket ticket) {
//        try {
//            if (ticket.getServiceType() == null || ticket.getServiceId() == null) {
//                return ResponseEntity.badRequest().body(Map.of("message", "Service type and service ID are required"));
//            }
//
//            Ticket savedTicket = ticketService.bookTicket(ticket);
//            return ResponseEntity.ok(savedTicket);
//        } catch (ResourceNotFoundException | BadRequestException e) {
//            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error booking ticket: " + e.getMessage()));
//        }
//    }
//
//    @PostMapping(value = "/payment", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> paymentRequest) {
//        try {
//            Ticket ticket = new Ticket();
//            ticket.setId(Long.parseLong(paymentRequest.get("id").toString()));
//            ticket.setTotalPrice(Double.parseDouble(paymentRequest.get("totalPrice").toString()));
//
//            String orderId = ticketService.createPaymentOrder(ticket);
//            return ResponseEntity.ok().body(Map.of("orderId", orderId));
//        } catch (RazorpayException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Payment Error: " + e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error creating payment order: " + e.getMessage()));
//        }
//    }
//
//    @PostMapping(value = "/updatePaymentStatus", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> updatePaymentStatus(@RequestBody Map<String, String> payload) {
//        try {
//            String ticketId = payload.get("ticketId");
//            String paymentId = payload.get("paymentId");
//
//            if (ticketId == null || paymentId == null) {
//                return ResponseEntity.badRequest().body(Map.of("message", "Ticket ID and Payment ID are required"));
//            }
//
//            ticketService.updateTicketPaymentStatus(Long.parseLong(ticketId), paymentId, "PAID");
//            return ResponseEntity.ok().body(Map.of("message", "Payment status updated successfully!"));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error updating payment status: " + e.getMessage()));
//        }
//    }
//
//    // ✅ FIXED: userId should be String
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<Ticket>> getTicketsByUser(@PathVariable String userId) {
//        try {
//            List<Ticket> tickets = ticketService.getTicketsByUserId(userId);
//            return ResponseEntity.ok(tickets);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
//        }
//    }
//}





//package com.ticketsystem.controller;
//
//import com.razorpay.RazorpayException;
//import com.ticketsystem.exception.BadRequestException;
//import com.ticketsystem.exception.ResourceNotFoundException;
//import com.ticketsystem.model.Ticket;
//import com.ticketsystem.payload.TicketDetailsDto;
//import com.ticketsystem.service.TicketService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import java.util.Collections;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/tickets")
//public class TicketController {
//
//    @Autowired
//    private TicketService ticketService;
//
//    @PostMapping(value = "/book", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> bookTicket(@RequestBody Ticket ticket) {
//        try {
//            if (ticket.getServiceType() == null || ticket.getServiceId() == null) {
//                Map<String, String> errorResponse = new HashMap<>();
//                errorResponse.put("message", "Service type and service ID are required");
//                System.out.println("Something went wrong to book ticket");
//                return ResponseEntity.badRequest().body(errorResponse);
//            }
//            
//            Ticket savedTicket = ticketService.bookTicket(ticket);
//            return ResponseEntity.ok(savedTicket);
//        } catch (ResourceNotFoundException | BadRequestException e) {
//            System.out.println("Something went wrong to book ticket in catch");
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(errorResponse);
//        } catch (Exception e) {
//            System.out.println("Something went wrong to book ticket in catch 2");
//
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("message", "Error booking ticket: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }
//
//    @PostMapping(value = "/payment", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> paymentRequest) {
//        try {
//            // Create a Ticket object from the request
//            Ticket ticket = new Ticket();
//            ticket.setId(Long.parseLong(paymentRequest.get("id").toString()));
//            ticket.setTotalPrice(Double.parseDouble(paymentRequest.get("totalPrice").toString()));
//            
//            String orderId = ticketService.createPaymentOrder(ticket);
//
//            return ResponseEntity.ok().body(Map.of("orderId", orderId));
//        } catch (RazorpayException e) {
//            System.out.println("Something went wrong to payment in catch");
//
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("message", "Payment Error: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        } catch (Exception e) {
//            System.out.println("Something went wrong to payment in catch 2");
//
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("message", "Error creating payment order: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }
//
//    @PostMapping(value = "/updatePaymentStatus", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> updatePaymentStatus(@RequestBody Map<String, String> payload) {
//        try {
//            String ticketId = payload.get("ticketId");
//            String paymentId = payload.get("paymentId");
//            
//            if (ticketId == null || paymentId == null) {
//                Map<String, String> errorResponse = new HashMap<>();
//                errorResponse.put("message", "Ticket ID and Payment ID are required");
//                return ResponseEntity.badRequest().body(errorResponse);
//            }
//            
//            ticketService.updateTicketPaymentStatus(Long.parseLong(ticketId), paymentId, "PAID");
//            return ResponseEntity.ok().body(Map.of("message", "Payment status updated successfully!"));
//        } catch (Exception e) {
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("message", "Error updating payment status: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }
//
//    
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<Ticket>> getTicketsByUser(@PathVariable Long userId) {
//        try {
//            List<Ticket> tickets = ticketService.getTicketsByUserId(userId);
//            return ResponseEntity.ok(tickets);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                                 .body(Collections.emptyList());
//        }
//    }

//    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> getTicketsByUser(@PathVariable String userId) {
//        try {
//            List<TicketDetailsDto> tickets = ticketService.getTicketsByUserId(userId);
//            return ResponseEntity.ok(tickets);
//        } catch (Exception e) {
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("message", "Error fetching tickets: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }
//}




//package com.ticketsystem.controller;
//
//import com.razorpay.RazorpayException;
//import com.ticketsystem.exception.BadRequestException;
//import com.ticketsystem.exception.ResourceNotFoundException;
//import com.ticketsystem.model.Ticket;
//import com.ticketsystem.payload.TicketDetailsDto;
//import com.ticketsystem.service.TicketService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/tickets")
//public class TicketController {
//
//    @Autowired
//    private TicketService ticketService;
//
//    @PostMapping("/book")
//    public ResponseEntity<?> bookTicket(@RequestBody Ticket ticket) {
//        try {
//            if (ticket.getServiceType() == null || ticket.getServiceId() == null) {
//                throw new BadRequestException("Service type and service ID are required");
//            }
//            
//            Ticket savedTicket = ticketService.bookTicket(ticket);
//            return ResponseEntity.ok(savedTicket);
//        } catch (ResourceNotFoundException | BadRequestException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body("Error booking ticket: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/payment")
//    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> paymentRequest) {
//        try {
//            // Create a Ticket object from the request
//            Ticket ticket = new Ticket();
//            ticket.setId(Long.parseLong(paymentRequest.get("id").toString()));
//            ticket.setTotalPrice(Double.parseDouble(paymentRequest.get("totalPrice").toString()));
//            
//            String orderId = ticketService.createPaymentOrder(ticket);
//            return ResponseEntity.ok(orderId);
//        } catch (RazorpayException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body("Payment Error: " + e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body("Error creating payment order: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/updatePaymentStatus")
//    public ResponseEntity<?> updatePaymentStatus(@RequestBody Map<String, String> payload) {
//        try {
//            String ticketId = payload.get("ticketId");
//            String paymentId = payload.get("paymentId");
//            
//            if (ticketId == null || paymentId == null) {
//                throw new BadRequestException("Ticket ID and Payment ID are required");
//            }
//            
//            ticketService.updateTicketPaymentStatus(Long.parseLong(ticketId), paymentId, "PAID");
//            return ResponseEntity.ok("Payment status updated successfully!");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body("Error updating payment status: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<?> getTicketsByUser(@PathVariable String userId) {
//        try {
//            List<TicketDetailsDto> tickets = ticketService.getTicketsByUserId(userId);
//            return ResponseEntity.ok(tickets);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body("Error fetching tickets: " + e.getMessage());
//        }
//    }
//}





//package com.ticketsystem.controller;
//
//import com.razorpay.RazorpayException;
//import com.ticketsystem.exception.BadRequestException;
//import com.ticketsystem.exception.ResourceNotFoundException;
//import com.ticketsystem.model.Ticket;
//import com.ticketsystem.payload.TicketDetailsDto; // NEW IMPORT
//import com.ticketsystem.service.TicketService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.*;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map; // NEW IMPORT
//
//@RestController
//@RequestMapping("/api/tickets")
//public class TicketController {
//
//    @Autowired
//    private TicketService ticketService;
//
//    @PostMapping("/book")
//    public ResponseEntity<?> bookTicket(@RequestBody Ticket ticket) {
//        try {
//            // Add validation for required fields
//            if (ticket.getServiceType() == null || ticket.getServiceId() == null) {
//                throw new BadRequestException("Service type and service ID are required");
//            }
//            
//            Ticket savedTicket = ticketService.bookTicket(ticket);
//            return ResponseEntity.ok(savedTicket);
//        } catch (ResourceNotFoundException | BadRequestException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body("Error booking ticket: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/payment")
//    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> paymentRequest) {
//        try {
//            // Extract data from request
//            Long ticketId = Long.parseLong(paymentRequest.get("id").toString());
//            Double totalPrice = Double.parseDouble(paymentRequest.get("totalPrice").toString());
//            
//            String orderId = ticketService.createPaymentOrder(ticketId, totalPrice);
//            return ResponseEntity.ok(orderId);
//        } catch (RazorpayException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body("Payment Error: " + e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body("Error creating payment order: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/updatePaymentStatus")
//    public ResponseEntity<?> updatePaymentStatus(@RequestBody Map<String, String> payload) {
//        try {
//            String ticketId = payload.get("ticketId");
//            String paymentId = payload.get("paymentId");
//            
//            if (ticketId == null || paymentId == null) {
//                throw new BadRequestException("Ticket ID and Payment ID are required");
//            }
//            
//            ticketService.updateTicketPaymentStatus(Long.parseLong(ticketId), paymentId, "PAID");
//            return ResponseEntity.ok("Payment status updated successfully!");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body("Error updating payment status: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<?> getTicketsByUser(@PathVariable String userId) {
//        try {
//            List<TicketDetailsDto> tickets = ticketService.getTicketsByUserId(userId);
//            return ResponseEntity.ok(tickets);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body("Error fetching tickets: " + e.getMessage());
//        }
//    }
//}

//@RestController
//@RequestMapping("/api/tickets")
//@CrossOrigin("*") // CORS handled by WebSecurityConfig now, but keep for clarity
//public class TicketController {
//
//    @Autowired
//    private TicketService ticketService;
//
//    @PostMapping("/book")
//    public ResponseEntity<?> bookTicket(@RequestBody Ticket ticket) {
//        try {
//            Ticket savedTicket = ticketService.bookTicket(ticket);
//            return ResponseEntity.ok(savedTicket);
//        } catch (ResourceNotFoundException | BadRequestException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (IllegalStateException e) { // For unauthenticated user trying to book
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error booking ticket: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/payment")
//    public ResponseEntity<?> createOrder(@RequestBody Ticket ticket) {
//        try {
//            String orderId = ticketService.createPaymentOrder(ticket);
//            return ResponseEntity.ok(orderId);
//        } catch (RazorpayException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment Error: " + e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating payment order: " + e.getMessage());
//        }
//    }
//
//    // NEW ENDPOINT: Update payment status after Razorpay callback
//    @PostMapping("/updatePaymentStatus")
//    public ResponseEntity<?> updatePaymentStatus(@RequestBody Map<String, String> payload) {
//        try {
//            String ticketId = payload.get("ticketId");
//            String paymentId = payload.get("paymentId"); // Razorpay payment ID
//            // You might want to add more validation here
//            ticketService.updateTicketPaymentStatus(Long.parseLong(ticketId), paymentId, "PAID");
//            return ResponseEntity.ok("Payment status updated successfully!");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating payment status: " + e.getMessage());
//        }
//    }
//
//    // MODIFIED RETURN TYPE
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<?> getTicketsByUser(@PathVariable String userId) {
//        try {
//            List<TicketDetailsDto> tickets = ticketService.getTicketsByUserId(userId); // MODIFIED CALL
//            return ResponseEntity.ok(tickets);
//        } catch (SecurityException e) { // For unauthorized access to other user's tickets
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
//        } catch (IllegalStateException e) { // For unauthenticated user
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching tickets: " + e.getMessage());
//        }
//    }
//}
