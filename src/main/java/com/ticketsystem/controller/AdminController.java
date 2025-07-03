package com.ticketsystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // NEW IMPORT
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class AdminController {

//    // This endpoint is just an example. You'd add actual admin functionalities here.
//    @GetMapping("/dashboard")
//    @PreAuthorize("hasRole('ADMIN')") // Ensures only users with ADMIN role can access
//    public ResponseEntity<String> getAdminDashboardData() {
//        return ResponseEntity.ok("Welcome, Admin! This is your dashboard data.");
//    }
    
 // This endpoint is just an example. You'd add actual admin functionalities here.
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')") // Ensures only users with ADMIN role can access
    public String getAdminDashboardData() {
        return "Welcome, Admin! This is your dashboard data.";
    }

    // You could add endpoints for managing users, services, etc.
    // @PostMapping("/users")
    // @PreAuthorize("hasRole('ADMIN')")
    // public ResponseEntity<?> createUser(@RequestBody User user) { ... }
}
