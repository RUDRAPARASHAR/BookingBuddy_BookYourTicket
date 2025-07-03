    package com.ticketsystem.controller;

    import com.ticketsystem.model.Movie;
    import com.ticketsystem.repository.MovieRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/api/movies")
    @CrossOrigin("*")
    public class MovieController {

        @Autowired
        private MovieRepository movieRepository;

        @PostMapping("/add")
        public Movie addMovie(@RequestBody Movie movie) {
            return movieRepository.save(movie);
        }

        @GetMapping
        public List<Movie> getAllMovies() {
            return movieRepository.findAll();
        }

        @GetMapping("/{id}")
        public Movie getMovieById(@PathVariable Long id) {
            return movieRepository.findById(id).orElse(null);
        }

        @GetMapping("/search")
        public List<Movie> searchMovies(
                @RequestParam(required = false) String title,
                @RequestParam(required = false) String theater,
                @RequestParam(required = false) String showTime) {
            if (theater != null && showTime != null) {
                return movieRepository.findByTheaterAndShowTime(theater, showTime);
            } else if (title != null) {
                return movieRepository.findByTitleContainingIgnoreCase(title);
            }
            return movieRepository.findAll();
        }
    }
    