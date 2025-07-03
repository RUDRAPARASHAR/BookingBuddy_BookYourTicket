    package com.ticketsystem.repository;

    import com.ticketsystem.model.Bus;
    import org.springframework.data.jpa.repository.JpaRepository;
    import java.util.List;

    public interface BusRepository extends JpaRepository<Bus, Long> {
        List<Bus> findByDepartureAndDestinationAndDepartureTime(String departure, String destination, String departureTime);
        // Add a method to find by departure and destination only (for broader search)
        List<Bus> findByDepartureAndDestination(String departure, String destination);
    }
    