package com.ticketsystem.service;

import com.ticketsystem.model.ERole;
import com.ticketsystem.model.Role;
import com.ticketsystem.model.User;
import com.ticketsystem.repository.RoleRepository;
import com.ticketsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        // Encode password before saving
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // Assign ROLE_USER if no role provided
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role USER is not found."));
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            user.setRoles(roles);
        }

        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}






//package com.ticketsystem.service;
//
//import com.ticketsystem.model.ERole;
//import com.ticketsystem.model.Role;
//import com.ticketsystem.model.User;
//import com.ticketsystem.repository.RoleRepository;
//import com.ticketsystem.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.HashSet;
//import java.util.Optional;
//import java.util.Set;
//
//@Service
//public class UserService {
//
//    @Autowired
//    UserRepository userRepository;
//
//    @Autowired
//    RoleRepository roleRepository;
//
//    @Autowired
//    PasswordEncoder passwordEncoder; // Injected PasswordEncoder
//
//    public User createUser(User user) {
//        // Encode password before saving
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//
//        // Assign default role
//        Set<Role> roles = new HashSet<>();
//        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
//                .orElseThrow(() -> new RuntimeException("Error: Role USER not found."));
//        roles.add(userRole);
//
//        user.setRoles(roles);
//
//        return userRepository.save(user);
//    }
//
//    public boolean existsByUsername(String username) {
//        return userRepository.existsByUsername(username);
//    }
//
//    public boolean existsByEmail(String email) {
//        return userRepository.existsByEmail(email);
//    }
//
//    public Optional<User> findByUsername(String username) {
//        return userRepository.findByUsername(username);
//    }
//}
//
//
//
////package com.ticketsystem.service;
////
////import com.ticketsystem.model.ERole;
////import com.ticketsystem.model.Role;
////import com.ticketsystem.model.User;
////import com.ticketsystem.repository.RoleRepository;
////import com.ticketsystem.repository.UserRepository;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.security.crypto.password.PasswordEncoder;
////import org.springframework.stereotype.Service;
////
////import java.util.HashSet;
////import java.util.Optional;
////import java.util.Set;
////
////@Service
////public class UserService {
////
////    @Autowired
////    UserRepository userRepository;
////
////    @Autowired
////    RoleRepository roleRepository;
////
////    @Autowired
////    PasswordEncoder passwordEncoder;
////
////    public User createUser(User user) {
////        // Encode password
////        user.setPassword(passwordEncoder.encode(user.getPassword()));
////
////        // Assign default role
////        Set<Role> roles = new HashSet<>();
////        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
////                .orElseThrow(() -> new RuntimeException("Error: Role USER not found."));
////        roles.add(userRole);
////
////        user.setRoles(roles);
////
////        return userRepository.save(user);
////    }
////
////    public boolean existsByUsername(String username) {
////        return userRepository.existsByUsername(username);
////    }
////
////    public boolean existsByEmail(String email) {
////        return userRepository.existsByEmail(email);
////    }
////
////    public Optional<User> findByUsername(String username) {
////        return userRepository.findByUsername(username);
////    }
////}
