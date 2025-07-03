    package com.ticketsystem.repository;

    import com.ticketsystem.model.Movie;
    import org.springframework.data.jpa.repository.JpaRepository;
    import java.util.List;

    public interface MovieRepository extends JpaRepository<Movie, Long> {
        List<Movie> findByTheaterAndShowTime(String theater, String showTime);
        // Add a method to find by title (for broader search)
        List<Movie> findByTitleContainingIgnoreCase(String title);
    }
    