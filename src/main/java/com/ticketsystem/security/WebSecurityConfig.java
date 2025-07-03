package com.ticketsystem.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .csrf(csrf -> csrf
        	    .ignoringRequestMatchers(
        	        new AntPathRequestMatcher("/api/auth/**"),
        	        new AntPathRequestMatcher("/api/tickets/book"),
        	        new AntPathRequestMatcher("/api/tickets/payment"),
        	        new AntPathRequestMatcher("/api/tickets/updatePaymentStatus"),
        	        new AntPathRequestMatcher("/api/tickets/my") // ✅ Add this
        	    )
        	)

            .cors(cors -> cors.configurationSource(corsConfigSource()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .invalidSessionUrl("/login?invalid-session=true")
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/index",
                    "/login",
                    "/signup",
                    "/api/auth/**"
                ).permitAll()
                .requestMatchers(
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/webjars/**"
                ).permitAll()
                .requestMatchers(
                    "/api/tickets/book",
                    "/api/tickets/payment",
                    "/api/tickets/updatePaymentStatus"
                ).permitAll()
                .requestMatchers(
                    "/booking",
                    "/my_tickets"
                ).authenticated()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/api/auth/signin")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/access-denied")
            )
            .authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
            "http://localhost:8080",
            "http://localhost:3000"
        ));
        config.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
        config.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-CSRF-TOKEN"
        ));
        config.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}





//package com.ticketsystem.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//public class WebSecurityConfig {
//
//    private final UserDetailsServiceImpl userDetailsService;
//    private final PasswordEncoder passwordEncoder;
//
//    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder) {
//        this.userDetailsService = userDetailsService;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder);
//        return authProvider;
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(csrf -> csrf
//                .ignoringRequestMatchers(
//                    "/api/auth/**", // All auth endpoints
//                    "/api/tickets/book",
//                    "/api/tickets/payment",
//                    "/api/tickets/updatePaymentStatus"
//                )
//            )
//            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//            .sessionManagement(session -> session
//                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//                .invalidSessionUrl("/login?invalid-session=true")
//            )
//            .authorizeHttpRequests(auth -> auth
//                // Public endpoints
//                .requestMatchers(
//                    "/",
//                    "/index",
//                    "/login",
//                    "/signup",
//                    "/api/auth/**"
//                ).permitAll()
//                
//                // Static resources
//                .requestMatchers(
//                    "/css/**",
//                    "/js/**",
//                    "/images/**",
//                    "/webjars/**"
//                ).permitAll()
//                
//                // Payment endpoints
//                .requestMatchers(
//                    "/api/tickets/book",
//                    "/api/tickets/payment",
//                    "/api/tickets/updatePaymentStatus"
//                ).permitAll()
//                
//                // Authenticated pages
//                .requestMatchers(
//                    "/booking",
//                    "/my_tickets"
//                ).authenticated()
//                
//                // Admin pages
//                .requestMatchers("/admin/**").hasRole("ADMIN")
//                
//                // API endpoints
//                .requestMatchers("/api/**").authenticated()
//                
//                // Fallback
//                .anyRequest().authenticated()
//            )
//            .formLogin(form -> form
//                .loginPage("/login")
//                .loginProcessingUrl("/api/auth/signin")
//                .defaultSuccessUrl("/", true)
//                .failureUrl("/login?error=true")
//                .permitAll()
//            )
//            .logout(logout -> logout
//                .logoutUrl("/api/auth/logout")
//                .logoutSuccessUrl("/login?logout=true")
//                .invalidateHttpSession(true)
//                .deleteCookies("JSESSIONID")
//                .permitAll()
//            )
//            .exceptionHandling(exception -> exception
//                .accessDeniedPage("/access-denied")
//            )
//            .authenticationProvider(authenticationProvider());
//
//        return http.build();
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of(
//            "http://localhost:8080",
//            "http://localhost:3000",
//            "https://your-production-domain.com"
//        ));
//        configuration.setAllowedMethods(Arrays.asList(
//            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
//        ));
//        configuration.setAllowedHeaders(Arrays.asList(
//            "Authorization",
//            "Content-Type",
//            "X-Requested-With",
//            "X-CSRF-TOKEN",
//            "Accept"
//        ));
//        configuration.setExposedHeaders(List.of(
//            "Authorization",
//            "Content-Disposition"
//        ));
//        configuration.setAllowCredentials(true);
//        configuration.setMaxAge(3600L);
//        
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//}



//package com.ticketsystem.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//public class WebSecurityConfig {
//
//    private final UserDetailsServiceImpl userDetailsService;
//    private final PasswordEncoder passwordEncoder;
//
//    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder) {
//        this.userDetailsService = userDetailsService;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
//            throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder);
//        return authProvider;
//    }
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(csrf -> csrf
//                .ignoringRequestMatchers(
//                    "/api/auth/signup", 
//                    "/api/auth/signin",
//                    "/api/tickets/book",  // Add this
//                    "/api/tickets/payment",  // Add this
//                    "/api/tickets/updatePaymentStatus"  // Add this
//                )
//            )
//            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/", "/index", "/login", "/signup").permitAll()
//                .requestMatchers("/api/auth/**").permitAll()
//                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
//                
//                // Allow ticket booking endpoints without authentication
//                .requestMatchers(
//                    "/api/tickets/book",
//                    "/api/tickets/payment",
//                    "/api/tickets/updatePaymentStatus"
//                ).permitAll()
//                
//                .requestMatchers("/booking", "/my_tickets").authenticated()
//                .requestMatchers("/admin").hasRole("ADMIN")
//                .anyRequest().authenticated()
//            )
//            // Rest of your configuration remains the same...
//            .formLogin(form -> form
//                .loginPage("/login")
//                .loginProcessingUrl("/api/auth/signin")
//                .defaultSuccessUrl("/", true)
//                .failureUrl("/login?error=true")
//                .permitAll()
//            )
//            .logout(logout -> logout
//                .logoutUrl("/api/auth/logout")
//                .logoutSuccessUrl("/login?logout=true")
//                .invalidateHttpSession(true)
//                .deleteCookies("JSESSIONID")
//                .permitAll()
//            )
//            .authenticationProvider(authenticationProvider());
//
//        return http.build();
//    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//            // IMPORTANT: CSRF for login processing URL
//            // If you are submitting a form, Spring Security expects a CSRF token.
//            // You can either include it in your form (recommended for production)
//            // or ignore CSRF for the login processing URL.
//            // For simplicity in this , we'll ignore it for the login processing URL.
//            // In a real application, you'd use <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
//            .csrf(csrf -> csrf
//                .ignoringRequestMatchers("/api/auth/signup", "/api/auth/signin") // Ignore CSRF for signup and signin
//            )
//            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
//            .authorizeHttpRequests(auth -> auth
//                // Allow access to static resources and public pages
//                .requestMatchers("/", "/index", "/login", "/signup").permitAll() // Public Thymeleaf pages
//                .requestMatchers("/api/auth/**").permitAll() // Auth API endpoints (signup, login processing)
//                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll() // Static resources
//
//                // Secure Thymeleaf pages based on roles/authentication
//                .requestMatchers("/booking", "/my_tickets").authenticated() // These pages require any authenticated user
//                .requestMatchers("/admin").hasRole("ADMIN") // Admin page requires ADMIN role
//
//                // Secure other API endpoints
//                .requestMatchers("/api/**").authenticated() // All other API endpoints require authentication
//                .anyRequest().authenticated() // All other requests require authentication (fallback)
//            )
//            .formLogin(form -> form
//                .loginPage("/login") // Use Thymeleaf login page
//                .loginProcessingUrl("/api/auth/signin") // THIS IS THE URL WHERE THE LOGIN FORM POSTS TO
//                .defaultSuccessUrl("/", true) // Redirect to index after successful login
//                .failureUrl("/login?error=true") // Redirect to login page with error on failure
//                .permitAll()
//            )
//            .logout(logout -> logout
//                .logoutUrl("/api/auth/logout") // URL to trigger logout
//                .logoutSuccessUrl("/login?logout=true") // Redirect to login page with logout message
//                .invalidateHttpSession(true) // Invalidate HTTP session
//                .deleteCookies("JSESSIONID") // Delete session cookie
//                .permitAll()
//            )
//            .authenticationProvider(authenticationProvider());
//
//        return http.build();
//    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        // Allow your frontend origin (Spring Boot default is 8080, if frontend is separate, add its URL)
//        configuration.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:3000")); // Example: if your frontend is on 3000
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-CSRF-TOKEN"));
//        configuration.setAllowCredentials(true);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//}





//package com.ticketsystem.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Configuration
//@EnableMethodSecurity
//public class WebSecurityConfig {
//
//    private final UserDetailsServiceImpl userDetailsService;
//    private final PasswordEncoder passwordEncoder;
//
//    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder) {
//        this.userDetailsService = userDetailsService;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
//            throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder);
//        return authProvider;
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/auth/signup")) // Allow signup without CSRF token
//            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
//            .authorizeHttpRequests(auth -> auth
//                // Allow access to static resources and public pages
//                .requestMatchers("/", "/index", "/login", "/signup").permitAll() // Public Thymeleaf pages
//                .requestMatchers("/api/auth/**").permitAll() // Auth API endpoints (signup, login processing)
//                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll() // Static resources
//
//                // Secure Thymeleaf pages based on roles/authentication
//                .requestMatchers("/booking", "/my_tickets").authenticated() // These pages require any authenticated user
//                .requestMatchers("/admin").hasRole("ADMIN") // Admin page requires ADMIN role
//
//                // Secure other API endpoints
//                .requestMatchers("/api/**").authenticated() // All other API endpoints require authentication
//                .anyRequest().authenticated() // All other requests require authentication (fallback)
//            )
//            .formLogin(form -> form
//                .loginPage("/login") // Use Thymeleaf login page
//                .loginProcessingUrl("/api/auth/signin") // URL to submit login form
//                .defaultSuccessUrl("/", true) // Redirect to index after successful login
//                .failureUrl("/login?error=true") // Redirect to login page with error on failure
//                .permitAll()
//            )
//            .logout(logout -> logout
//                .logoutUrl("/api/auth/logout") // URL to trigger logout
//                .logoutSuccessUrl("/login?logout=true") // Redirect to login page with logout message
//                .invalidateHttpSession(true) // Invalidate HTTP session
//                .deleteCookies("JSESSIONID") // Delete session cookie
//                .permitAll()
//            )
//            .authenticationProvider(authenticationProvider());
//
//        return http.build();
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        // Allow your frontend origin (Spring Boot default is 8080, if frontend is separate, add its URL)
//        configuration.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:3000")); // Example: if your frontend is on 3000
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-CSRF-TOKEN"));
//        configuration.setAllowCredentials(true);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//}
//
//
//
////   package com.ticketsystem.security;
////
////   import org.springframework.context.annotation.Bean;
////   import org.springframework.context.annotation.Configuration;
////   import org.springframework.security.authentication.AuthenticationManager;
////   import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
////   import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
////   import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // NEW IMPORT
////   import org.springframework.security.config.annotation.web.builders.HttpSecurity;
////   import org.springframework.security.config.http.SessionCreationPolicy;
////   import org.springframework.security.crypto.password.PasswordEncoder;
////   import org.springframework.security.web.SecurityFilterChain;
////   import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
////   import org.springframework.web.cors.CorsConfiguration;
////   import org.springframework.web.cors.CorsConfigurationSource;
////   import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
////
////   import java.util.Arrays;
////   import java.util.List;
////
////   @Configuration
////   @EnableMethodSecurity // NEW: Enable method-level security for @PreAuthorize
////   public class WebSecurityConfig {
////
////       private final UserDetailsServiceImpl userDetailsService;
////       private final PasswordEncoder passwordEncoder;
////
////       public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder) {
////           this.userDetailsService = userDetailsService;
////           this.passwordEncoder = passwordEncoder;
////       }
////
////       @Bean
////       public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
////               throws Exception {
////           return config.getAuthenticationManager();
////       }
////
////       @Bean
////       public DaoAuthenticationProvider authenticationProvider() {
////           DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
////           authProvider.setUserDetailsService(userDetailsService);
////           authProvider.setPasswordEncoder(passwordEncoder);
////           return authProvider;
////       }
////
////       @Bean
////       public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
////           http
////               .csrf(csrf -> csrf.ignoringRequestMatchers("/api/auth/signup"))
////               .cors(cors -> cors.configurationSource(corsConfigurationSource()))
////               .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
////               .authorizeHttpRequests(auth -> auth
////                   // Allow access to static resources and public pages
////                   .requestMatchers("/", "/index", "/login", "/signup", "/booking", "/my_tickets", "/admin").permitAll() // Thymeleaf pages
////                   .requestMatchers("/api/auth/**").permitAll() // Auth API endpoints
////                   .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll() // Static resources
////                   .requestMatchers("/admin.html").hasRole("ADMIN") // This is for direct HTML access, but we'll use /admin Thymeleaf now
////                   .requestMatchers("/booking.html", "/my_tickets.html").authenticated() // This is for direct HTML access, but we'll use /booking, /my_tickets Thymeleaf now
////                   .requestMatchers("/api/**").authenticated() // Secure other API endpoints
////                   .anyRequest().authenticated() // All other requests require authentication
////               )
////               .formLogin(form -> form
////                   .loginPage("/login") // Use Thymeleaf login page
////                   .loginProcessingUrl("/api/auth/signin")
////                   .defaultSuccessUrl("/", true) // Redirect to index after successful login
////                   .failureUrl("/login?error=true")
////                   .permitAll()
////               )
////               .logout(logout -> logout
////                   .logoutUrl("/api/auth/logout")
////                   .logoutSuccessUrl("/login?logout=true")
////                   .invalidateHttpSession(true)
////                   .deleteCookies("JSESSIONID")
////                   .permitAll()
////               )
////               .authenticationProvider(authenticationProvider());
////
////           return http.build();
////       }
////
////       @Bean
////       public CorsConfigurationSource corsConfigurationSource() {
////           CorsConfiguration configuration = new CorsConfiguration();
////           configuration.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:8080")); // Allow your frontend origin (Spring Boot default is 8081)
////           configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
////           configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-CSRF-TOKEN"));
////           configuration.setAllowCredentials(true);
////           UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
////           source.registerCorsConfiguration("/**", configuration);
////           return source;
////       }
////   }
////   
////    // package com.ticketsystem.security;
////
////    // import org.springframework.context.annotation.Bean;
////    // import org.springframework.context.annotation.Configuration;
////    // import org.springframework.security.authentication.AuthenticationManager;
////    // import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
////    // import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
////    // import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
////    // import org.springframework.security.config.annotation.web.builders.HttpSecurity;
////    // import org.springframework.security.config.http.SessionCreationPolicy;
////    // import org.springframework.security.crypto.password.PasswordEncoder;
////    // import org.springframework.security.web.SecurityFilterChain;
////    // import org.springframework.web.cors.CorsConfiguration;
////    // import org.springframework.web.cors.CorsConfigurationSource;
////    // import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
////
////    // import java.util.Arrays;
////    // import java.util.List;
////
////    // @Configuration
////    // @EnableMethodSecurity
////    // public class WebSecurityConfig {
////
////    //     private final UserDetailsServiceImpl userDetailsService;
////    //     private final PasswordEncoder passwordEncoder;
////
////    //     public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder) {
////    //         this.userDetailsService = userDetailsService;
////    //         this.passwordEncoder = passwordEncoder;
////    //     }
////
////    //     @Bean
////    //     public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
////    //             throws Exception {
////    //         return config.getAuthenticationManager();
////    //     }
////
////    //     @Bean
////    //     public DaoAuthenticationProvider authenticationProvider() {
////    //         DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
////    //         authProvider.setUserDetailsService(userDetailsService);
////    //         authProvider.setPasswordEncoder(passwordEncoder);
////    //         return authProvider;
////    //     }
////
////    //     @Bean
////    //     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
////    //         http
////    //             .csrf(csrf -> csrf.ignoringRequestMatchers("/api/auth/signup"))
////    //             .cors(cors -> cors.configurationSource(corsConfigurationSource()))
////    //             .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
////    //             .authorizeHttpRequests(auth -> auth
////    //                 // Allow access to static resources and public pages
////    //                 .requestMatchers("/", "/index", "/login", "/signup").permitAll() // Public pages
////    //                 .requestMatchers("/api/auth/**").permitAll() // Auth API endpoints
////    //                 .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll() // Static resources
////
////    //                 // Secure Thymeleaf pages based on roles/authentication
////    //                 .requestMatchers("/booking", "/my_tickets").authenticated() // These pages require any authenticated user
////    //                 .requestMatchers("/admin").hasRole("ADMIN") // Admin page requires ADMIN role
////
////    //                 // Secure other API endpoints
////    //                 .requestMatchers("/api/**").authenticated() // All other API endpoints require authentication
////    //                 .anyRequest().authenticated() // All other requests require authentication (fallback)
////    //             )
////    //             .formLogin(form -> form
////    //                 .loginPage("/login") // Use Thymeleaf login page
////    //                 .loginProcessingUrl("/api/auth/signin")
////    //                 .defaultSuccessUrl("/", true) // Redirect to index after successful login
////    //                 .failureUrl("/login?error=true")
////    //                 .permitAll()
////    //             )
////    //             .logout(logout -> logout
////    //                 .logoutUrl("/api/auth/logout")
////    //                 .logoutSuccessUrl("/login?logout=true")
////    //                 .invalidateHttpSession(true)
////    //                 .deleteCookies("JSESSIONID")
////    //                 .permitAll()
////    //             )
////    //             .authenticationProvider(authenticationProvider());
////
////    //         return http.build();
////    //     }
////
////    //     @Bean
////    //     public CorsConfigurationSource corsConfigurationSource() {
////    //         CorsConfiguration configuration = new CorsConfiguration();
////    //         configuration.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:8080")); // Allow your frontend origin (Spring Boot default is 8081)
////    //         configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
////    //         configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-CSRF-TOKEN"));
////    //         configuration.setAllowCredentials(true);
////    //         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
////    //         source.registerCorsConfiguration("/**", configuration);
////    //         return source;
////    //     }
////    // }
//    