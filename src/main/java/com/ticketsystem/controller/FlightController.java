    package com.ticketsystem.controller;

    import com.ticketsystem.model.Flight;
    import com.ticketsystem.repository.FlightRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/api/flights")
    @CrossOrigin("*")
    public class FlightController {

        @Autowired
        private FlightRepository flightRepository;

        @PostMapping("/add")
        public Flight addFlight(@RequestBody Flight flight) {
            return flightRepository.save(flight);
        }

        @GetMapping
        public List<Flight> getAllFlights() {
            return flightRepository.findAll();
        }

        @GetMapping("/{id}")
        public Flight getFlightById(@PathVariable Long id) {
            return flightRepository.findById(id).orElse(null);
        }

        @GetMapping("/search")
        public List<Flight> searchFlights(
                @RequestParam(required = false) String departure,
                @RequestParam(required = false) String destination,
                @RequestParam(required = false) String departureTime) {
            if (departure != null && destination != null && departureTime != null) {
                return flightRepository.findByDepartureAndDestinationAndDepartureTime(departure, destination, departureTime);
            } else if (departure != null && destination != null) {
                return flightRepository.findByDepartureAndDestination(departure, destination);
            }
            return flightRepository.findAll();
        }
    }
    