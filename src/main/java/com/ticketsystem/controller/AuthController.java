package com.ticketsystem.controller;

import com.ticketsystem.model.User;
import com.ticketsystem.payload.SignupRequest;
import com.ticketsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        // Check if username or email already exists
        if (userService.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }

        if (userService.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already in use!");
        }

        // ✅ Encode password before saving
        String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());
        
        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(), encodedPassword);
//        
//        User user = new User(
//                signUpRequest.getUsername(),
//                signUpRequest.getEmail(),
//                encodedPassword
//        );

        // Call service to save user
        userService.createUser(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    // Login is handled automatically by Spring Security formLogin (no need for @PostMapping("/signin"))
}


//    package com.ticketsystem.controller;
//
//    import com.ticketsystem.model.User;
//    import com.ticketsystem.payload.SignupRequest;
//    import com.ticketsystem.service.UserService;
//    import org.springframework.beans.factory.annotation.Autowired;
//    import org.springframework.http.ResponseEntity;
//    import org.springframework.web.bind.annotation.*;
//
//    @RestController
//    @RequestMapping("/api/auth")
//    @CrossOrigin("*")
//    public class AuthController {
//
//        @Autowired
//        UserService userService;
//
//        // The /signin endpoint will now be handled by Spring Security's formLogin configuration.
//        // You can remove the @PostMapping("/signin") method entirely from here.
//        // Spring Security will automatically intercept POST requests to /api/auth/signin
//        // (or whatever you configure as loginProcessingUrl) and handle authentication.
//
//        @PostMapping("/signup")
//        public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
//            if (userService.existsByUsername(signUpRequest.getUsername())) {
//                return ResponseEntity.badRequest().body("Username is already taken!");
//            }
//
//            if (userService.existsByEmail(signUpRequest.getEmail())) {
//                return ResponseEntity.badRequest().body("Email is already in use!");
//            }
//
//            User user = new User(
//                signUpRequest.getUsername(),
//                signUpRequest.getEmail(),
//                signUpRequest.getPassword()
//            );
//
//            userService.createUser(user);
//            return ResponseEntity.ok("User registered successfully!");
//        }
//    }
//    