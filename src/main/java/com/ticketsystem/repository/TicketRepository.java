package com.ticketsystem.repository;

import com.ticketsystem.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUserId(String userId);

    long countByPaymentStatus(String status);

    List<Ticket> findAllByOrderByBookingDateDesc();
}

//
//public interface TicketRepository extends JpaRepository<Ticket, Long> {
//    List<Ticket> findByUserId(String userId); // ✅ FIXED: String instead of Long
//    long countByPaymentStatus(String status);
//
//    List<Ticket> findAllByOrderByBookingDateDesc();
//
//}



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