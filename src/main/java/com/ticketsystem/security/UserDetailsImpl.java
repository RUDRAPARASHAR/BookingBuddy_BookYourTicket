//    package com.ticketsystem.security;
//
//    import com.ticketsystem.model.User;
//    import org.springframework.security.core.GrantedAuthority;
//    import org.springframework.security.core.authority.SimpleGrantedAuthority;
//    import org.springframework.security.core.userdetails.UserDetails;
//    import java.util.*;
//    import java.util.stream.Collectors;
//
//    public class UserDetailsImpl implements UserDetails {
//
//        private Long id;
//        private String username;
//        private String email;
//        private String password;
//        private Collection<? extends GrantedAuthority> authorities;
//
//        public UserDetailsImpl(Long id, String username, String email, String password,
//                               Collection<? extends GrantedAuthority> authorities) {
//            this.id = id;
//            this.username = username;
//            this.email = email;
//            this.password = password;
//            this.authorities = authorities;
//        }
//
//        public static UserDetailsImpl build(User user) {
//            List<GrantedAuthority> authorities = user.getRoles().stream()
//                    .map(role -> new SimpleGrantedAuthority(role.getName().name()))
//                    .collect(Collectors.toList());
//
//            return new UserDetailsImpl(
//                    user.getId(),
//                    user.getUsername(),
//                    user.getEmail(),
//                    user.getPassword(),
//                    authorities
//            );
//        }
//
//        @Override
//        public Collection<? extends GrantedAuthority> getAuthorities() {
//            return authorities;
//        }
//
//        @Override public String getPassword() { return password; }
//        @Override public String getUsername() { return username; }
//        @Override public boolean isAccountNonExpired() { return true; }
//        @Override public boolean isAccountNonLocked() { return true; }
//        @Override public boolean isCredentialsNonExpired() { return true; }
//        @Override public boolean isEnabled() { return true; }
//
//        public Long getId() { return id; }
//        public String getEmail() { return email; }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//            UserDetailsImpl that = (UserDetailsImpl) o;
//            return Objects.equals(id, that.id);
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(id);
//        }
//    }
//    

package com.ticketsystem.security;

import com.ticketsystem.model.Role;
import com.ticketsystem.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String username, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    // ✅ Add this method to fix your error
    public Long getId() {
        return id;
    }
    
    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
        		.map(role -> new SimpleGrantedAuthority(role.getName().name())) // assumes role.getName() returns "ROLE_USER", etc.
            .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                authorities);
    }

    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password; // ✅ must return encoded password
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
