    package com.ticketsystem.repository;

    import com.ticketsystem.model.Flight;
    import org.springframework.data.jpa.repository.JpaRepository;
    import java.util.List;

    public interface FlightRepository extends JpaRepository<Flight, Long> {
        List<Flight> findByDepartureAndDestinationAndDepartureTime(String departure, String destination, String departureTime);
        // Add a method to find by departure and destination only
        List<Flight> findByDepartureAndDestination(String departure, String destination);
    }
    