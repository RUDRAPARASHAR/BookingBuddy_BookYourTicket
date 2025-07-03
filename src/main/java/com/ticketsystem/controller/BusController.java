    package com.ticketsystem.controller;

    import com.ticketsystem.model.Bus;
    import com.ticketsystem.repository.BusRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/api/buses")
    @CrossOrigin("*")
    public class BusController {

        @Autowired
        private BusRepository busRepository;

        @PostMapping("/add")
        public Bus addBus(@RequestBody Bus bus) {
            return busRepository.save(bus);
        }

        @GetMapping
        public List<Bus> getAllBuses() {
            return busRepository.findAll();
        }

        @GetMapping("/{id}")
        public Bus getBusById(@PathVariable Long id) {
            return busRepository.findById(id).orElse(null);
        }

        @GetMapping("/search")
        public List<Bus> searchBuses(
                @RequestParam(required = false) String departure,
                @RequestParam(required = false) String destination,
                @RequestParam(required = false) String departureTime) {
            if (departure != null && destination != null && departureTime != null) {
                return busRepository.findByDepartureAndDestinationAndDepartureTime(departure, destination, departureTime);
            } else if (departure != null && destination != null) {
                return busRepository.findByDepartureAndDestination(departure, destination);
            }
            return busRepository.findAll(); // Return all if no specific search criteria
        }
    }
    