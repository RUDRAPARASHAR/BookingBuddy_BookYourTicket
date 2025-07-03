package com.ticketsystem;

import com.ticketsystem.model.*;
import com.ticketsystem.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class TicketBookingSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(TicketBookingSystemApplication.class, args);
    }

    @Bean
    public CommandLineRunner demoData(
            UserRepository userRepository,
            RoleRepository roleRepository,
            BusRepository busRepository,
            TrainRepository trainRepository,
            FlightRepository flightRepository,
            MovieRepository movieRepository,
            EventRepository eventRepository,
            PasswordEncoder passwordEncoder) { // PasswordEncoder is correctly injected here
        
        return args -> {
            // 1. Create Roles
            if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
                roleRepository.save(new Role(ERole.ROLE_USER));
            }
            if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
                roleRepository.save(new Role(ERole.ROLE_ADMIN));
            }

            // 2. Create Default Users
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User("admin", "admin@example.com", passwordEncoder.encode("adminpass")); // Password encoded
                Set<Role> adminRoles = new HashSet<>();
                roleRepository.findByName(ERole.ROLE_ADMIN).ifPresent(adminRoles::add);
                roleRepository.findByName(ERole.ROLE_USER).ifPresent(adminRoles::add);
                admin.setRoles(adminRoles);
                userRepository.save(admin);
            }

            if (userRepository.findByUsername("user").isEmpty()) {
                User user = new User("user", "user@example.com", passwordEncoder.encode("userpass")); // Password encoded
                Set<Role> userRoles = new HashSet<>();
                roleRepository.findByName(ERole.ROLE_USER).ifPresent(userRoles::add);
                user.setRoles(userRoles);
                userRepository.save(user);
            }

            // 3. Populate Sample Data (rest of your data population)
            if (busRepository.count() == 0) {
                busRepository.save(new Bus("Delhi", "Jaipur", "Royal Travels", "2025-07-15T08:00", 40, 850.0));
                busRepository.save(new Bus("Mumbai", "Pune", "GreenLine", "2025-07-16T10:30", 35, 600.0));
            }

            if (trainRepository.count() == 0) {
                trainRepository.save(new Train("12345", "Delhi", "Mumbai", "2025-07-17T18:00", 100, 1500.0));
                trainRepository.save(new Train("54321", "Chennai", "Bangalore", "2025-07-18T09:00", 80, 1200.0));
            }

            if (flightRepository.count() == 0) {
                flightRepository.save(new Flight("AI101", "Air India", "Delhi", "Bangalore", "2025-07-19T10:00", 150, 5000.0));
                flightRepository.save(new Flight("6E202", "IndiGo", "Mumbai", "Chennai", "2025-07-20T14:00", 120, 3500.0));
            }

            if (movieRepository.count() == 0) {
                movieRepository.save(new Movie("Avatar", "PVR Cinemas", "2025-07-21T19:00", 200, 250.0));
                movieRepository.save(new Movie("Inception", "INOX", "2025-07-22T21:30", 180, 280.0));
            }

            if (eventRepository.count() == 0) {
                eventRepository.save(new Event("Music Festival", "DLF Arena", "2025-07-23T17:00", 5000, 1200.0));
                eventRepository.save(new Event("Tech Conference", "Convention Center", "2025-07-24T10:00", 300, 2000.0));
            }
        };
    }
}





//package com.ticketsystem;
//
//import com.ticketsystem.model.*;
//import com.ticketsystem.repository.*;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.HashSet;
//import java.util.Set;
//
//@SpringBootApplication
//public class TicketBookingSystemApplication {
//    public static void main(String[] args) {
//        SpringApplication.run(TicketBookingSystemApplication.class, args);
//    }
//
//    @Bean
//    public CommandLineRunner demoData(
//            UserRepository userRepository,
//            RoleRepository roleRepository,
//            BusRepository busRepository,
//            TrainRepository trainRepository,
//            FlightRepository flightRepository,
//            MovieRepository movieRepository,
//            EventRepository eventRepository,
//            PasswordEncoder passwordEncoder) {
//        
//        return args -> {
//            // 1. Create Roles
//            if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
//                roleRepository.save(new Role(ERole.ROLE_USER));
//            }
//            if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
//                roleRepository.save(new Role(ERole.ROLE_ADMIN));
//            }
//
//            // 2. Create Default Users
//            if (userRepository.findByUsername("admin").isEmpty()) {
//                User admin = new User("admin", "admin@example.com", passwordEncoder.encode("adminpass"));
//                Set<Role> adminRoles = new HashSet<>();
//                roleRepository.findByName(ERole.ROLE_ADMIN).ifPresent(adminRoles::add);
//                roleRepository.findByName(ERole.ROLE_USER).ifPresent(adminRoles::add);
//                admin.setRoles(adminRoles);
//                userRepository.save(admin);
//            }
//
//            if (userRepository.findByUsername("user").isEmpty()) {
//                User user = new User("user", "user@example.com", passwordEncoder.encode("userpass"));
//                Set<Role> userRoles = new HashSet<>();
//                roleRepository.findByName(ERole.ROLE_USER).ifPresent(userRoles::add);
//                user.setRoles(userRoles);
//                userRepository.save(user);
//            }
//
//            // 3. Populate Sample Data
//            if (busRepository.count() == 0) {
//                busRepository.save(new Bus("Delhi", "Jaipur", "Royal Travels", "2025-07-15T08:00", 40, 850.0));
//                busRepository.save(new Bus("Mumbai", "Pune", "GreenLine", "2025-07-16T10:30", 35, 600.0));
//            }
//
//            if (trainRepository.count() == 0) {
//                trainRepository.save(new Train("12345", "Delhi", "Mumbai", "2025-07-17T18:00", 100, 1500.0));
//                trainRepository.save(new Train("54321", "Chennai", "Bangalore", "2025-07-18T09:00", 80, 1200.0));
//            }
//
//            if (flightRepository.count() == 0) {
//                flightRepository.save(new Flight("AI101", "Air India", "Delhi", "Bangalore", "2025-07-19T10:00", 150, 5000.0));
//                flightRepository.save(new Flight("6E202", "IndiGo", "Mumbai", "Chennai", "2025-07-20T14:00", 120, 3500.0));
//            }
//
//            if (movieRepository.count() == 0) {
//                movieRepository.save(new Movie("Avatar", "PVR Cinemas", "2025-07-21T19:00", 200, 250.0));
//                movieRepository.save(new Movie("Inception", "INOX", "2025-07-22T21:30", 180, 280.0));
//            }
//
//            if (eventRepository.count() == 0) {
//                eventRepository.save(new Event("Music Festival", "DLF Arena", "2025-07-23T17:00", 5000, 1200.0));
//                eventRepository.save(new Event("Tech Conference", "Convention Center", "2025-07-24T10:00", 300, 2000.0));
//            }
//        };
//    }
//}
//
//
//
//
//
////
////package com.ticketsystem;
////
////import com.ticketsystem.model.*;
////import com.ticketsystem.repository.*;
////import org.springframework.boot.CommandLineRunner;
////import org.springframework.boot.SpringApplication;
////import org.springframework.boot.autoconfigure.SpringBootApplication;
////import org.springframework.context.annotation.Bean;
////import org.springframework.security.crypto.password.PasswordEncoder;
////
////import java.time.LocalDateTime;
////import java.util.HashSet;
////import java.util.Set;
////
////@SpringBootApplication
////public class TicketBookingSystemApplication {
////    public static void main(String[] args) {
////        SpringApplication.run(TicketBookingSystemApplication.class, args);
////    }
////
////    @Bean
////    public CommandLineRunner demoData(
////            UserRepository userRepository,
////            RoleRepository roleRepository,
////            BusRepository busRepository,
////            TrainRepository trainRepository,
////            FlightRepository flightRepository,
////            MovieRepository movieRepository,
////            EventRepository eventRepository,
////            PasswordEncoder passwordEncoder) {
////        return args -> {
////            // 1. Create Roles if they don't exist
////            if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
////                roleRepository.save(new Role(ERole.ROLE_USER));
////            }
////            if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
////                roleRepository.save(new Role(ERole.ROLE_ADMIN));
////            }
////
////            // 2. Create Default Users if they don't exist
////            // Admin User
////            if (userRepository.findByUsername("admin").isEmpty()) {
////                User admin = new User("admin", "admin@example.com", passwordEncoder.encode("adminpass"));
////                Set<Role> adminRoles = new HashSet<>();
////                roleRepository.findByName(ERole.ROLE_ADMIN).ifPresent(adminRoles::add);
////                roleRepository.findByName(ERole.ROLE_USER).ifPresent(adminRoles::add); // Admin also has USER role
////                admin.setRoles(adminRoles);
////                userRepository.save(admin);
////                System.out.println("Created Admin User: admin/adminpass");
////            }
////
////            // Regular User
////            if (userRepository.findByUsername("user").isEmpty()) {
////                User regularUser = new User("user", "user@example.com", passwordEncoder.encode("userpass"));
////                Set<Role> userRoles = new HashSet<>();
////                roleRepository.findByName(ERole.ROLE_USER).ifPresent(userRoles::add);
////                regularUser.setRoles(userRoles);
////                userRepository.save(regularUser);
////                System.out.println("Created Regular User: user/userpass");
////            }
////
////            // 3. Populate Sample Service Data if tables are empty
////            // Buses
////            if (busRepository.count() == 0) {
////                busRepository.save(new Bus(null, "Delhi", "Jaipur", "RedBus", "2025-07-10T08:00", 40, 850.0));
////                busRepository.save(new Bus(null, "Jaipur", "Agra", "BlueLine", "2025-07-10T12:30", 35, 600.0));
////                busRepository.save(new Bus(null, "Mumbai", "Pune", "GreenTravels", "2025-07-11T06:00", 50, 400.0));
////                System.out.println("Populated sample Bus data.");
////            }
////
////            // Trains
////            if (trainRepository.count() == 0) {
////                trainRepository.save(new Train(null, "12345", "Delhi", "Mumbai", "2025-07-12T18:00", 100, 1500.0));
////                trainRepository.save(new Train(null, "67890", "Mumbai", "Chennai", "2025-07-13T09:00", 80, 2200.0));
////                trainRepository.save(new Train(null, "11223", "Kolkata", "Delhi", "2025-07-14T20:00", 120, 1800.0));
////                System.out.println("Populated sample Train data.");
////            }
////
////            // Flights
////            if (flightRepository.count() == 0) {
////                flightRepository.save(new Flight(null, "AI101", "Air India", "Delhi", "Bangalore", "2025-07-15T10:00", 150, 4500.0));
////                flightRepository.save(new Flight(null, "6E202", "IndiGo", "Bangalore", "Hyderabad", "2025-07-16T14:00", 120, 3200.0));
////                flightRepository.save(new Flight(null, "UK303", "Vistara", "Hyderabad", "Mumbai", "2025-07-17T07:30", 100, 3800.0));
////                System.out.println("Populated sample Flight data.");
////            }
////
////            // Movies
////            if (movieRepository.count() == 0) {
////                movieRepository.save(new Movie(null, "Action Hero", "PVR Cinemas", "2025-07-18T19:00", 200, 250.0));
////                movieRepository.save(new Movie(null, "Romantic Comedy", "INOX Multiplex", "2025-07-18T21:30", 180, 280.0));
////                movieRepository.save(new Movie(null, "Sci-Fi Adventure", "Cinepolis", "2025-07-19T16:00", 150, 220.0));
////                System.out.println("Populated sample Movie data.");
////            }
////
////            // Events
////            if (eventRepository.count() == 0) {
////                eventRepository.save(new Event(null, "Music Fest 2025", "JLN Stadium, Delhi", "2025-08-01T17:00", 5000, 1200.0));
////                eventRepository.save(new Event(null, "Comedy Night Live", "Town Hall, Mumbai", "2025-07-20T20:00", 300, 700.0));
////                eventRepository.save(new Event(null, "Art Exhibition", "Gallery One, Bangalore", "2025-07-25T11:00", 100, 150.0));
////                System.out.println("Populated sample Event data.");
////            }
////        };
////    }
////}
//
////
////
////
////
////package com.ticketsystem;
////
////import org.springframework.boot.SpringApplication;
////import org.springframework.boot.autoconfigure.SpringBootApplication;
////
////@SpringBootApplication
////public class TicketBookingSystemApplication {
////    public static void main(String[] args) {
////        SpringApplication.run(TicketBookingSystemApplication.class, args);
////    }
////}
