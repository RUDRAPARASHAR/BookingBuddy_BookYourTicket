    package com.ticketsystem.controller;

    import com.ticketsystem.model.User;
    import com.ticketsystem.security.UserDetailsImpl;
    import com.ticketsystem.service.TicketService;
    import com.ticketsystem.service.UserService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestParam;

    import java.util.Optional;

    @Controller
    public class FrontendController {

        @Autowired
        private UserService userService; // To get user details for the header

        @Autowired
        private TicketService ticketService; // To fetch tickets for my_tickets page

        // Helper to add common model attributes (e.g., username, roles)
        private void addCommonAttributes(Model model) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                model.addAttribute("loggedIn", true);
                model.addAttribute("username", userDetails.getUsername());
                model.addAttribute("isAdmin", userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
                model.addAttribute("currentUserId", userDetails.getId().toString()); // Pass user ID for my_tickets
            } else {
                model.addAttribute("loggedIn", false);
                model.addAttribute("username", "Guest");
                model.addAttribute("isAdmin", false);
                model.addAttribute("currentUserId", null);
            }
        }

        @GetMapping("/")
        public String home(Model model) {
            addCommonAttributes(model);
            return "index"; // Refers to src/main/resources/templates/index.html
        }

        @GetMapping("/login")
        public String login(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
            if (error != null) {
                model.addAttribute("errorMessage", "Invalid username or password.");
            }
            if (logout != null) {
                model.addAttribute("successMessage", "You have been logged out successfully.");
            }
            addCommonAttributes(model); // Still add common attributes for header
            return "login"; // Refers to src/main/resources/templates/login.html
        }

        @GetMapping("/signup")
        public String signup(Model model) {
            addCommonAttributes(model);
            return "signup"; // Refers to src/main/resources/templates/signup.html
        }

        @GetMapping("/booking")
        public String booking(@RequestParam(value = "service", required = false) String serviceType, Model model) {
            addCommonAttributes(model);
            model.addAttribute("selectedService", serviceType != null ? serviceType : "flights");
            return "booking"; // Refers to src/main/resources/templates/booking.html
        }

        @GetMapping("/my_tickets")
        public String myTickets(Model model) {
            addCommonAttributes(model);
            // The actual fetching of tickets will happen via AJAX on the client-side
            // or you could pre-fetch them here if you prefer server-side rendering for the list.
            // For this example, we'll keep the AJAX fetch for tickets as it's more dynamic.
            return "my_tickets"; // Refers to src/main/resources/templates/my_tickets.html
        }

        @GetMapping("/admin")
        public String admin(Model model) {
            addCommonAttributes(model);
            // Spring Security's @PreAuthorize("hasRole('ADMIN')") on AdminController
            // will handle access. This just serves the page.
            return "admin"; // Refers to src/main/resources/templates/admin.html
        }
    }
    