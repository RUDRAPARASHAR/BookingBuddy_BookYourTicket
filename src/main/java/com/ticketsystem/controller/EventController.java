    package com.ticketsystem.controller;

    import com.ticketsystem.model.Event;
    import com.ticketsystem.repository.EventRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/api/events")
    @CrossOrigin("*")
    public class EventController {

        @Autowired
        private EventRepository eventRepository;

        @PostMapping("/add")
        public Event addEvent(@RequestBody Event event) {
            return eventRepository.save(event);
        }

        @GetMapping
        public List<Event> getAllEvents() {
            return eventRepository.findAll();
        }

        @GetMapping("/{id}")
        public Event getEventById(@PathVariable Long id) {
            return eventRepository.findById(id).orElse(null);
        }

        @GetMapping("/search")
        public List<Event> searchEvents(
                @RequestParam(required = false) String name,
                @RequestParam(required = false) String location,
                @RequestParam(required = false) String startTime) { // Changed dateTime to startTime
            if (location != null && startTime != null) { // Changed dateTime to startTime
                return eventRepository.findByLocationAndStartTime(location, startTime); // Changed findByLocationAndDateTime to findByLocationAndStartTime
            } else if (name != null) {
                return eventRepository.findByNameContainingIgnoreCase(name);
            }
            return eventRepository.findAll();
        }
    }
    


//    package com.ticketsystem.controller;
//
//    import com.ticketsystem.model.Event;
//    import com.ticketsystem.repository.EventRepository;
//    import org.springframework.beans.factory.annotation.Autowired;
//    import org.springframework.web.bind.annotation.*;
//
//    import java.util.List;
//
//    @RestController
//    @RequestMapping("/api/events")
//    @CrossOrigin("*")
//    public class EventController {
//
//        @Autowired
//        private EventRepository eventRepository;
//
//        @PostMapping("/add")
//        public Event addEvent(@RequestBody Event event) {
//            return eventRepository.save(event);
//        }
//
//        @GetMapping
//        public List<Event> getAllEvents() {
//            return eventRepository.findAll();
//        }
//
//        @GetMapping("/{id}")
//        public Event getEventById(@PathVariable Long id) {
//            return eventRepository.findById(id).orElse(null);
//        }
//
//        @GetMapping("/search")
//        public List<Event> searchEvents(
//                @RequestParam(required = false) String name,
//                @RequestParam(required = false) String location,
//                @RequestParam(required = false) String dateTime) {
//            if (location != null && dateTime != null) {
//                return eventRepository.findByLocationAndDateTime(location, dateTime);
//            } else if (name != null) {
//                return eventRepository.findByNameContainingIgnoreCase(name);
//            }
//            return eventRepository.findAll();
//        }
//    }
//    