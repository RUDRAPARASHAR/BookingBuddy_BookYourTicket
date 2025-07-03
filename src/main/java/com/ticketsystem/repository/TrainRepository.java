    package com.ticketsystem.repository;

    import com.ticketsystem.model.Train;
    import org.springframework.data.jpa.repository.JpaRepository;
    import java.util.List;

    public interface TrainRepository extends JpaRepository<Train, Long> {
        List<Train> findByDepartureAndDestinationAndDepartureTime(String departure, String destination, String departureTime);
        // Add a method to find by departure and destination only
        List<Train> findByDepartureAndDestination(String departure, String destination);
    }
    