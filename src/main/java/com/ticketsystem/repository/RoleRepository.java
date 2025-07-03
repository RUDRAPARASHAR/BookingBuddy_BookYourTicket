    package com.ticketsystem.repository;

    import com.ticketsystem.model.ERole;
    import com.ticketsystem.model.Role;
    import org.springframework.data.jpa.repository.JpaRepository;

    import java.util.Optional;

    public interface RoleRepository extends JpaRepository<Role, Long> { // Changed Integer to Long
        Optional<Role> findByName(ERole name);
    }
    

//package com.ticketsystem.repository;
//
//import com.ticketsystem.model.ERole;
//import com.ticketsystem.model.Role;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//
//public interface RoleRepository extends JpaRepository<Role, Integer> {
//    Optional<Role> findByName(ERole name);
//}
