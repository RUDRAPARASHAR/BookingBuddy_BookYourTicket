    package com.ticketsystem.repository;

    import com.ticketsystem.model.Event;
    import org.springframework.data.jpa.repository.JpaRepository;
    import java.util.List;

    public interface EventRepository extends JpaRepository<Event, Long> {
        List<Event> findByLocationAndStartTime(String location, String startTime); // Changed DateTime to StartTime
        // Add a method to find by name (for broader search)
        List<Event> findByNameContainingIgnoreCase(String name);
    }
    



//    package com.ticketsystem.repository;
//
//    import com.ticketsystem.model.Event;
//    import org.springframework.data.jpa.repository.JpaRepository;
//    import java.util.List;
//
//    public interface EventRepository extends JpaRepository<Event, Long> {
//        List<Event> findByLocationAndDateTime(String location, String dateTime);
//        // Add a method to find by name (for broader search)
//        List<Event> findByNameContainingIgnoreCase(String name);
//    }
//    