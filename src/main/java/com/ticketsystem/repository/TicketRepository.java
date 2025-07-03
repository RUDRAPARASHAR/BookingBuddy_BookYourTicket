package com.ticketsystem.repository;

import com.ticketsystem.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUserId(String userId); // ✅ FIXED: String instead of Long
}



//package com.ticketsystem.repository;
//
//import com.ticketsystem.model.Ticket;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//
//public interface TicketRepository extends JpaRepository<Ticket, Long> {
////    List<Ticket> findByUserId(String userId);
//    List<Ticket> findByUserId(Long userId);
//
//    List<Ticket> findByServiceTypeAndServiceId(String serviceType, String serviceId);
//}