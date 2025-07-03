    package com.ticketsystem.controller;

    import com.ticketsystem.model.Train;
    import com.ticketsystem.repository.TrainRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/api/trains")
    @CrossOrigin("*")
    public class TrainController {

        @Autowired
        private TrainRepository trainRepository;

        @PostMapping("/add")
        public Train addTrain(@RequestBody Train train) {
            return trainRepository.save(train);
        }

        @GetMapping
        public List<Train> getAllTrains() {
            return trainRepository.findAll();
        }

        @GetMapping("/{id}")
        public Train getTrainById(@PathVariable Long id) {
            return trainRepository.findById(id).orElse(null);
        }

        @GetMapping("/search")
        public List<Train> searchTrains(
                @RequestParam(required = false) String departure,
                @RequestParam(required = false) String destination,
                @RequestParam(required = false) String departureTime) {
            if (departure != null && destination != null && departureTime != null) {
                return trainRepository.findByDepartureAndDestinationAndDepartureTime(departure, destination, departureTime);
            } else if (departure != null && destination != null) {
                return trainRepository.findByDepartureAndDestination(departure, destination);
            }
            return trainRepository.findAll();
        }
    }
    