package com.ticketsystem.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.ticketsystem.exception.BadRequestException;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.model.*;
import com.ticketsystem.payload.TicketDetailsDto;
import com.ticketsystem.repository.*;
import com.ticketsystem.security.UserDetailsImpl;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketService {

    @Autowired private TicketRepository ticketRepository;
    @Autowired private BusRepository busRepository;
    @Autowired private TrainRepository trainRepository;
    @Autowired private FlightRepository flightRepository;
    @Autowired private MovieRepository movieRepository;
    @Autowired private EventRepository eventRepository;
    @Autowired private UserRepository userRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
            authentication.getPrincipal() instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) authentication.getPrincipal()).getId().toString();
        }
        throw new IllegalStateException("User not authenticated.");
    }

    public Ticket bookTicket(Ticket ticket) {
        ticket.setUserId(getCurrentUserId());

        switch (ticket.getServiceType().toLowerCase()) {
            case "bus":
                Bus bus = busRepository.findById(Long.parseLong(ticket.getServiceId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));
                if (bus.getAvailableSeats() < ticket.getQuantity()) {
                    throw new BadRequestException("Not enough seats available");
                }
                bus.setAvailableSeats(bus.getAvailableSeats() - ticket.getQuantity());
                busRepository.save(bus);
                break;
            case "train":
                Train train = trainRepository.findById(Long.parseLong(ticket.getServiceId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Train not found"));
                if (train.getAvailableSeats() < ticket.getQuantity()) {
                    throw new BadRequestException("Not enough seats available");
                }
                train.setAvailableSeats(train.getAvailableSeats() - ticket.getQuantity());
                trainRepository.save(train);
                break;
            case "flight":
                Flight flight = flightRepository.findById(Long.parseLong(ticket.getServiceId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Flight not found"));
                if (flight.getAvailableSeats() < ticket.getQuantity()) {
                    throw new BadRequestException("Not enough seats available");
                }
                flight.setAvailableSeats(flight.getAvailableSeats() - ticket.getQuantity());
                flightRepository.save(flight);
                break;
            case "movie":
                Movie movie = movieRepository.findById(Long.parseLong(ticket.getServiceId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
                if (movie.getAvailableSeats() < ticket.getQuantity()) {
                    throw new BadRequestException("Not enough seats available");
                }
                movie.setAvailableSeats(movie.getAvailableSeats() - ticket.getQuantity());
                movieRepository.save(movie);
                break;
            case "event":
                Event event = eventRepository.findById(Long.parseLong(ticket.getServiceId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
                if (event.getCapacity() < ticket.getQuantity()) {
                    throw new BadRequestException("Not enough seats available");
                }
                event.setCapacity(event.getCapacity() - ticket.getQuantity());
                eventRepository.save(event);
                break;
            default:
                throw new BadRequestException("Invalid service type");
        }

        ticket.setBookingDate(LocalDateTime.now());
        ticket.setPaymentStatus("PENDING");
        return ticketRepository.save(ticket);
    }

    public String createPaymentOrder(Ticket ticket) throws RazorpayException {
        RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int)(ticket.getTotalPrice() * 100));
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "order_rcptid_" + ticket.getId());
        Order order = razorpay.orders.create(orderRequest);
        return order.get("id");
    }

    public void updateTicketPaymentStatus(Long ticketId, String paymentId, String status) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        ticket.setPaymentStatus(status);
        ticket.setPaymentId(paymentId);
        ticketRepository.save(ticket);
    }

    public List<Ticket> getTicketsByUserId(String userId) {
        return ticketRepository.findByUserId(userId);
    }

    public List<TicketDetailsDto> getTicketDTOsByUserId(String userId) {
        if (!getCurrentUserId().equals(userId)) {
            throw new SecurityException("Access denied");
        }
        return ticketRepository.findByUserId(userId).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    private TicketDetailsDto convertToDto(Ticket ticket) {
        TicketDetailsDto dto = new TicketDetailsDto();
        dto.setId(ticket.getId());
        dto.setServiceType(ticket.getServiceType());
        dto.setServiceId(ticket.getServiceId());
        dto.setQuantity(ticket.getQuantity());
        dto.setTotalPrice(ticket.getTotalPrice());
        dto.setPaymentStatus(ticket.getPaymentStatus());
        dto.setBookingDate(ticket.getBookingDate());

        switch (ticket.getServiceType().toLowerCase()) {
            case "bus":
                busRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(bus -> {
                    dto.setServiceName(bus.getOperator() + " Bus");
                    dto.setServiceRoute(bus.getDeparture() + " to " + bus.getDestination());
                    dto.setServiceTime(bus.getDepartureTime());
                });
                break;
            case "train":
                trainRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(train -> {
                    dto.setServiceName("Train " + train.getTrainNumber());
                    dto.setServiceRoute(train.getDeparture() + " to " + train.getDestination());
                    dto.setServiceTime(train.getDepartureTime());
                });
                break;
            case "flight":
                flightRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(flight -> {
                    dto.setServiceName(flight.getAirline() + " Flight " + flight.getFlightNumber());
                    dto.setServiceRoute(flight.getDeparture() + " to " + flight.getDestination());
                    dto.setServiceTime(flight.getDepartureTime());
                });
                break;
            case "movie":
                movieRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(movie -> {
                    dto.setServiceName(movie.getTitle());
                    dto.setServiceRoute("Theater: " + movie.getTheater());
                    dto.setServiceTime("Showtime: " + movie.getShowTime());
                });
                break;
            case "event":
                eventRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(event -> {
                    dto.setServiceName(event.getName());
                    dto.setServiceRoute("Location: " + event.getLocation());
                    dto.setServiceTime("Date/Time: " + event.getStartTime());
                });
                break;
        }

        return dto;
    }
}







//package com.ticketsystem.service;
//
//import com.razorpay.Order;
//import com.razorpay.RazorpayClient;
//import com.razorpay.RazorpayException;
//import com.ticketsystem.exception.BadRequestException;
//import com.ticketsystem.exception.ResourceNotFoundException;
//import com.ticketsystem.model.*;
//import com.ticketsystem.payload.TicketDetailsDto;
//import com.ticketsystem.repository.*;
//import com.ticketsystem.security.UserDetailsImpl;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.*;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//public class TicketService {
//
//    @Autowired private TicketRepository ticketRepository;
//    @Autowired private BusRepository busRepository;
//    @Autowired private TrainRepository trainRepository;
//    @Autowired private FlightRepository flightRepository;
//    @Autowired private MovieRepository movieRepository;
//    @Autowired private EventRepository eventRepository;
//    @Autowired private UserRepository userRepository;
//
//    @Value("${razorpay.key.id}")
//    private String razorpayKeyId;
//
//    @Value("${razorpay.key.secret}")
//    private String razorpayKeySecret;
//
//    private String getCurrentUserId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated() &&
//            authentication.getPrincipal() instanceof UserDetailsImpl) {
//            return ((UserDetailsImpl) authentication.getPrincipal()).getId().toString();
//        }
//        throw new IllegalStateException("User not authenticated.");
//    }
//
//    public Ticket bookTicket(Ticket ticket) {
//        ticket.setUserId(getCurrentUserId());
//
//        switch (ticket.getServiceType().toLowerCase()) {
//            case "bus":
//                Bus bus = busRepository.findById(Long.parseLong(ticket.getServiceId()))
//                    .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));
//                if (bus.getAvailableSeats() < ticket.getQuantity()) {
//                    throw new BadRequestException("Not enough seats available");
//                }
//                bus.setAvailableSeats(bus.getAvailableSeats() - ticket.getQuantity());
//                busRepository.save(bus);
//                break;
//            case "train":
//                Train train = trainRepository.findById(Long.parseLong(ticket.getServiceId()))
//                    .orElseThrow(() -> new ResourceNotFoundException("Train not found"));
//                if (train.getAvailableSeats() < ticket.getQuantity()) {
//                    throw new BadRequestException("Not enough seats available");
//                }
//                train.setAvailableSeats(train.getAvailableSeats() - ticket.getQuantity());
//                trainRepository.save(train);
//                break;
//            case "flight":
//                Flight flight = flightRepository.findById(Long.parseLong(ticket.getServiceId()))
//                    .orElseThrow(() -> new ResourceNotFoundException("Flight not found"));
//                if (flight.getAvailableSeats() < ticket.getQuantity()) {
//                    throw new BadRequestException("Not enough seats available");
//                }
//                flight.setAvailableSeats(flight.getAvailableSeats() - ticket.getQuantity());
//                flightRepository.save(flight);
//                break;
//            case "movie":
//                Movie movie = movieRepository.findById(Long.parseLong(ticket.getServiceId()))
//                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
//                if (movie.getAvailableSeats() < ticket.getQuantity()) {
//                    throw new BadRequestException("Not enough seats available");
//                }
//                movie.setAvailableSeats(movie.getAvailableSeats() - ticket.getQuantity());
//                movieRepository.save(movie);
//                break;
//            case "event":
//                Event event = eventRepository.findById(Long.parseLong(ticket.getServiceId()))
//                    .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
//                if (event.getCapacity() < ticket.getQuantity()) {
//                    throw new BadRequestException("Not enough seats available");
//                }
//                event.setCapacity(event.getCapacity() - ticket.getQuantity());
//                eventRepository.save(event);
//                break;
//            default:
//                throw new BadRequestException("Invalid service type");
//        }
//
//        ticket.setBookingDate(LocalDateTime.now());
//        ticket.setPaymentStatus("PENDING");
//        return ticketRepository.save(ticket);
//    }
//
//    public String createPaymentOrder(Ticket ticket) throws RazorpayException {
//        RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
//        JSONObject orderRequest = new JSONObject();
//        orderRequest.put("amount", (int)(ticket.getTotalPrice() * 100));
//        orderRequest.put("currency", "INR");
//        orderRequest.put("receipt", "order_rcptid_" + ticket.getId());
//        Order order = razorpay.orders.create(orderRequest);
//        return order.get("id");
//    }
//
//    public void updateTicketPaymentStatus(Long ticketId, String paymentId, String status) {
//        Ticket ticket = ticketRepository.findById(ticketId)
//            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
//        ticket.setPaymentStatus(status);
//        ticket.setPaymentId(paymentId);
//        ticketRepository.save(ticket);
//    }
//
//    // ✅ Corrected method using String
////    public List<Ticket> getTicketsByUserId(String userId) {
////        return ticketRepository.findByUserId(userId);
////    }
////
////    public List<TicketDetailsDto> getTicketDTOsByUserId(String userId) {
////        if (!getCurrentUserId().equals(userId)) {
////            throw new SecurityException("Access denied");
////        }
////        return ticketRepository.findByUserId(userId).stream()
////            .map(this::convertToDto)
////            .collect(Collectors.toList());
////    }
//    public List<TicketDetailsDto> getTicketDTOsByUserId(String userId) {
//        if (!getCurrentUserId().equals(userId)) {
//            throw new SecurityException("Access denied");
//        }
//        return ticketRepository.findByUserId(userId).stream()
//            .map(this::convertToDto)
//            .collect(Collectors.toList());
//    }
//
//
//    private TicketDetailsDto convertToDto(Ticket ticket) {
//        TicketDetailsDto dto = new TicketDetailsDto();
//        dto.setId(ticket.getId());
//        dto.setServiceType(ticket.getServiceType());
//        dto.setServiceId(ticket.getServiceId());
//        dto.setQuantity(ticket.getQuantity());
//        dto.setTotalPrice(ticket.getTotalPrice());
//        dto.setPaymentStatus(ticket.getPaymentStatus());
//        dto.setBookingDate(ticket.getBookingDate());
//
//        switch (ticket.getServiceType().toLowerCase()) {
//            case "bus":
//                busRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(bus -> {
//                    dto.setServiceName(bus.getOperator() + " Bus");
//                    dto.setServiceRoute(bus.getDeparture() + " to " + bus.getDestination());
//                    dto.setServiceTime(bus.getDepartureTime());
//                });
//                break;
//            case "train":
//                trainRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(train -> {
//                    dto.setServiceName("Train " + train.getTrainNumber());
//                    dto.setServiceRoute(train.getDeparture() + " to " + train.getDestination());
//                    dto.setServiceTime(train.getDepartureTime());
//                });
//                break;
//            case "flight":
//                flightRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(flight -> {
//                    dto.setServiceName(flight.getAirline() + " Flight " + flight.getFlightNumber());
//                    dto.setServiceRoute(flight.getDeparture() + " to " + flight.getDestination());
//                    dto.setServiceTime(flight.getDepartureTime());
//                });
//                break;
//            case "movie":
//                movieRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(movie -> {
//                    dto.setServiceName(movie.getTitle());
//                    dto.setServiceRoute("Theater: " + movie.getTheater());
//                    dto.setServiceTime("Showtime: " + movie.getShowTime());
//                });
//                break;
//            case "event":
//                eventRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(event -> {
//                    dto.setServiceName(event.getName());
//                    dto.setServiceRoute("Location: " + event.getLocation());
//                    dto.setServiceTime("Date/Time: " + event.getStartTime());
//                });
//                break;
//        }
//
//        return dto;
//    }
//}





//package com.ticketsystem.service;
//
//import com.razorpay.Order;
//import com.razorpay.RazorpayClient;
//import com.razorpay.RazorpayException;
//import com.ticketsystem.exception.BadRequestException;
//import com.ticketsystem.exception.ResourceNotFoundException;
//import com.ticketsystem.model.*;
//import com.ticketsystem.payload.TicketDetailsDto;
//import com.ticketsystem.repository.*;
//import com.ticketsystem.security.UserDetailsImpl;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.*;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class TicketService {
//
//    @Autowired private TicketRepository ticketRepository;
//    @Autowired private BusRepository busRepository;
//    @Autowired private TrainRepository trainRepository;
//    @Autowired private FlightRepository flightRepository;
//    @Autowired private MovieRepository movieRepository;
//    @Autowired private EventRepository eventRepository;
//    @Autowired private UserRepository userRepository;
//
//    @Value("${razorpay.key.id}")
//    private String razorpayKeyId;
//
//    @Value("${razorpay.key.secret}")
//    private String razorpayKeySecret;
//
//    private String getCurrentUserId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated() && 
//            authentication.getPrincipal() instanceof UserDetailsImpl) {
//            return ((UserDetailsImpl) authentication.getPrincipal()).getId().toString();
//        }
//        throw new IllegalStateException("User not authenticated.");
//    }
//
//    public Ticket bookTicket(Ticket ticket) {
//        ticket.setUserId(getCurrentUserId());
//
//        switch (ticket.getServiceType().toLowerCase()) {
//            case "bus":
//                Bus bus = busRepository.findById(Long.parseLong(ticket.getServiceId()))
//                    .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));
//                if (bus.getAvailableSeats() < ticket.getQuantity()) {
//                    throw new BadRequestException("Not enough seats available");
//                }
//                bus.setAvailableSeats(bus.getAvailableSeats() - ticket.getQuantity());
//                busRepository.save(bus);
//                break;
//            case "train":
//                Train train = trainRepository.findById(Long.parseLong(ticket.getServiceId()))
//                    .orElseThrow(() -> new ResourceNotFoundException("Train not found"));
//                if (train.getAvailableSeats() < ticket.getQuantity()) {
//                    throw new BadRequestException("Not enough seats available");
//                }
//                train.setAvailableSeats(train.getAvailableSeats() - ticket.getQuantity());
//                trainRepository.save(train);
//                break;
//            case "flight":
//                Flight flight = flightRepository.findById(Long.parseLong(ticket.getServiceId()))
//                    .orElseThrow(() -> new ResourceNotFoundException("Flight not found"));
//                if (flight.getAvailableSeats() < ticket.getQuantity()) {
//                    throw new BadRequestException("Not enough seats available");
//                }
//                flight.setAvailableSeats(flight.getAvailableSeats() - ticket.getQuantity());
//                flightRepository.save(flight);
//                break;
//            case "movie":
//                Movie movie = movieRepository.findById(Long.parseLong(ticket.getServiceId()))
//                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
//                if (movie.getAvailableSeats() < ticket.getQuantity()) {
//                    throw new BadRequestException("Not enough seats available");
//                }
//                movie.setAvailableSeats(movie.getAvailableSeats() - ticket.getQuantity());
//                movieRepository.save(movie);
//                break;
//            case "event":
//                Event event = eventRepository.findById(Long.parseLong(ticket.getServiceId()))
//                    .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
//                if (event.getCapacity() < ticket.getQuantity()) {
//                    throw new BadRequestException("Not enough seats available");
//                }
//                event.setCapacity(event.getCapacity() - ticket.getQuantity());
//                eventRepository.save(event);
//                break;
//            default:
//                throw new BadRequestException("Invalid service type");
//        }
//
//        ticket.setBookingDate(LocalDateTime.now());
//        ticket.setPaymentStatus("PENDING");
//        return ticketRepository.save(ticket);
//    }
//
//    public String createPaymentOrder(Ticket ticket) throws RazorpayException {
//        RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
//        System.out.println(razorpay);
//        JSONObject orderRequest = new JSONObject();
//        orderRequest.put("amount", (int)(ticket.getTotalPrice() * 100));
//        orderRequest.put("currency", "INR");
//        orderRequest.put("receipt", "order_rcptid_" + ticket.getId());
//        Order order = razorpay.orders.create(orderRequest);
//        return order.get("id");
//    }
//
//    public void updateTicketPaymentStatus(Long ticketId, String paymentId, String status) {
//        Ticket ticket = ticketRepository.findById(ticketId)
//            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
//        ticket.setPaymentStatus(status);
//        ticketRepository.save(ticket);
//    }
//    public List<Ticket> getTicketsByUserId(Long userId) {
//        return ticketRepository.findByUserId(userId);
//    }
//
//
//    public List<TicketDetailsDto> getTicketsByUserId(String userId) {
//        if (!getCurrentUserId().equals(userId)) {
//            throw new SecurityException("Access denied");
//        }
//        return ticketRepository.findByUserId(userId).stream()
//            .map(this::convertToDto)
//            .collect(Collectors.toList());
//    }
//
//    private TicketDetailsDto convertToDto(Ticket ticket) {
//        TicketDetailsDto dto = new TicketDetailsDto();
//        dto.setId(ticket.getId());
//        dto.setServiceType(ticket.getServiceType());
//        dto.setServiceId(ticket.getServiceId());
//        dto.setQuantity(ticket.getQuantity());
//        dto.setTotalPrice(ticket.getTotalPrice());
//        dto.setPaymentStatus(ticket.getPaymentStatus());
//        dto.setBookingDate(ticket.getBookingDate());
//
//        switch (ticket.getServiceType().toLowerCase()) {
//            case "bus":
//                busRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(bus -> {
//                    dto.setServiceName(bus.getOperator() + " Bus");
//                    dto.setServiceRoute(bus.getDeparture() + " to " + bus.getDestination());
//                    dto.setServiceTime(bus.getDepartureTime());
//                });
//                break;
//            case "train":
//                trainRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(train -> {
//                    dto.setServiceName("Train " + train.getTrainNumber());
//                    dto.setServiceRoute(train.getDeparture() + " to " + train.getDestination());
//                    dto.setServiceTime(train.getDepartureTime());
//                });
//                break;
//            case "flight":
//                flightRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(flight -> {
//                    dto.setServiceName(flight.getAirline() + " Flight " + flight.getFlightNumber());
//                    dto.setServiceRoute(flight.getDeparture() + " to " + flight.getDestination());
//                    dto.setServiceTime(flight.getDepartureTime());
//                });
//                break;
//            case "movie":
//                movieRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(movie -> {
//                    dto.setServiceName(movie.getTitle());
//                    dto.setServiceRoute("Theater: " + movie.getTheater());
//                    dto.setServiceTime("Showtime: " + movie.getShowTime());
//                });
//                break;
//            case "event":
//                eventRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(event -> {
//                    dto.setServiceName(event.getName());
//                    dto.setServiceRoute("Location: " + event.getLocation());
//                    dto.setServiceTime("Date/Time: " + event.getStartTime());
//                });
//                break;
//        }
//
//        return dto;
//    }
//}





//package com.ticketsystem.service;
//
//import com.razorpay.Order;
//import com.razorpay.RazorpayClient;
//import com.razorpay.RazorpayException;
//import com.ticketsystem.exception.BadRequestException;
//import com.ticketsystem.exception.ResourceNotFoundException;
//import com.ticketsystem.model.*;
//import com.ticketsystem.payload.TicketDetailsDto;
//import com.ticketsystem.repository.*;
//import com.ticketsystem.security.UserDetailsImpl;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.*;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class TicketService {
//
//    @Autowired private TicketRepository ticketRepository;
//    @Autowired private BusRepository busRepository;
//    @Autowired private TrainRepository trainRepository;
//    @Autowired private FlightRepository flightRepository;
//    @Autowired private MovieRepository movieRepository;
//    @Autowired private EventRepository eventRepository;
//    @Autowired private UserRepository userRepository;
//
//    @Value("${razorpay.key.id}")
//    private String razorpayKeyId;
//
//    @Value("${razorpay.key.secret}")
//    private String razorpayKeySecret;
//
//    private String getCurrentUserId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated() && 
//            authentication.getPrincipal() instanceof UserDetailsImpl) {
//            return ((UserDetailsImpl) authentication.getPrincipal()).getId().toString();
//        }
//        throw new IllegalStateException("User not authenticated.");
//    }
//
//    public Ticket bookTicket(Ticket ticket) {
//        ticket.setUserId(getCurrentUserId());
//
//        switch (ticket.getServiceType().toLowerCase()) {
//            case "bus":
//                Bus bus = busRepository.findById(Long.parseLong(ticket.getServiceId()))
//                    .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));
//                validateAndUpdateSeats(bus, ticket.getQuantity());
//                busRepository.save(bus);
//                break;
//            case "train":
//                Train train = trainRepository.findById(Long.parseLong(ticket.getServiceId()))
//                    .orElseThrow(() -> new ResourceNotFoundException("Train not found"));
//                validateAndUpdateSeats(train, ticket.getQuantity());
//                trainRepository.save(train);
//                break;
//            // Similar cases for flight, movie, event...
//            default:
//                throw new BadRequestException("Invalid service type");
//        }
//
//        ticket.setBookingDate(LocalDateTime.now());
//        ticket.setPaymentStatus("PENDING");
//        return ticketRepository.save(ticket);
//    }
//
//    private void validateAndUpdateSeats(ServiceWithSeats service, int quantity) {
//        if (service.getAvailableSeats() < quantity) {
//            throw new BadRequestException("Not enough seats available");
//        }
//        service.setAvailableSeats(service.getAvailableSeats() - quantity);
//    }
//
//    public String createPaymentOrder(Ticket ticket) throws RazorpayException {
//        RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
//        JSONObject orderRequest = new JSONObject();
//        orderRequest.put("amount", (int)(ticket.getTotalPrice() * 100));
//        orderRequest.put("currency", "INR");
//        orderRequest.put("receipt", "order_rcptid_" + ticket.getId());
//        Order order = razorpay.orders.create(orderRequest);
//        return order.get("id");
//    }
//
//    public void updateTicketPaymentStatus(Long ticketId, String paymentId, String status) {
//        Ticket ticket = ticketRepository.findById(ticketId)
//            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
//        ticket.setPaymentStatus(status);
//        ticketRepository.save(ticket);
//    }
//
//    public List<TicketDetailsDto> getTicketsByUserId(String userId) {
//        if (!getCurrentUserId().equals(userId)) {
//            throw new SecurityException("Access denied");
//        }
//        return ticketRepository.findByUserId(userId).stream()
//            .map(this::convertToDto)
//            .collect(Collectors.toList());
//    }
//
//    private TicketDetailsDto convertToDto(Ticket ticket) {
//        TicketDetailsDto dto = new TicketDetailsDto();
//        // Set all ticket properties
//        // Add service-specific details
//        dto.setId(ticket.getId());
//        dto.setServiceType(ticket.getServiceType());
//        dto.setServiceId(ticket.getServiceId());
//        dto.setQuantity(ticket.getQuantity());
//        dto.setTotalPrice(ticket.getTotalPrice());
//        dto.setPaymentStatus(ticket.getPaymentStatus());
//        dto.setBookingDate(ticket.getBookingDate());
//
//        switch (ticket.getServiceType().toLowerCase()) {
//            case "bus" -> busRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(bus -> {
//                dto.setServiceName(bus.getOperator() + " Bus");
//                dto.setServiceRoute(bus.getDeparture() + " to " + bus.getDestination());
//                dto.setServiceTime(bus.getDepartureTime());
//            });
//            case "train" -> trainRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(train -> {
//                dto.setServiceName("Train " + train.getTrainNumber());
//                dto.setServiceRoute(train.getDeparture() + " to " + train.getDestination());
//                dto.setServiceTime(train.getDepartureTime());
//            });
//            case "flight" -> flightRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(flight -> {
//                dto.setServiceName(flight.getAirline() + " Flight " + flight.getFlightNumber());
//                dto.setServiceRoute(flight.getDeparture() + " to " + flight.getDestination());
//                dto.setServiceTime(flight.getDepartureTime());
//            });
//            case "movie" -> movieRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(movie -> {
//                dto.setServiceName(movie.getTitle());
//                dto.setServiceRoute("Theater: " + movie.getTheater());
//                dto.setServiceTime("Showtime: " + movie.getShowTime());
//            });
//            case "event" -> eventRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(event -> {
//                dto.setServiceName(event.getName());
//                dto.setServiceRoute("Location: " + event.getLocation());
//                dto.setServiceTime("Date/Time: " + event.getStartTime()); // ✅ FIXED FIELD
//            });
//        }
//        return dto;
//    }
//}



//package com.ticketsystem.service;
//
//import com.razorpay.Order;
//import com.razorpay.RazorpayClient;
//import com.razorpay.RazorpayException;
//import com.ticketsystem.exception.BadRequestException;
//import com.ticketsystem.exception.ResourceNotFoundException;
//import com.ticketsystem.model.*;
//import com.ticketsystem.payload.TicketDetailsDto;
//import com.ticketsystem.repository.*;
//import com.ticketsystem.security.UserDetailsImpl;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.*;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class TicketService {
//
//    @Autowired private TicketRepository ticketRepository;
//    @Autowired private BusRepository busRepository;
//    @Autowired private TrainRepository trainRepository;
//    @Autowired private FlightRepository flightRepository;
//    @Autowired private MovieRepository movieRepository;
//    @Autowired private EventRepository eventRepository;
//    @Autowired private UserRepository userRepository;
//
//    @Value("${razorpay.key.id}")
//    private String razorpayKeyId;
//
//    @Value("${razorpay.key.secret}")
//    private String razorpayKeySecret;
//
//    // Get current authenticated user's ID
//    private String getCurrentUserId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetailsImpl) {
//            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//            return userDetails.getId().toString();
//        }
//        throw new IllegalStateException("User not authenticated.");
//    }
//
//    public Ticket bookTicket(Ticket ticket) {
//        ticket.setUserId(getCurrentUserId());
//
//        switch (ticket.getServiceType().toLowerCase()) {
//            case "bus" -> {
//                Bus bus = busRepository.findById(Long.parseLong(ticket.getServiceId()))
//                        .orElseThrow(() -> new ResourceNotFoundException("Bus not found with ID: " + ticket.getServiceId()));
//                if (bus.getAvailableSeats() < ticket.getQuantity())
//                    throw new BadRequestException("Not enough seats available for Bus ID: " + ticket.getServiceId());
//                bus.setAvailableSeats(bus.getAvailableSeats() - ticket.getQuantity());
//                busRepository.save(bus);
//            }
//            case "train" -> {
//                Train train = trainRepository.findById(Long.parseLong(ticket.getServiceId()))
//                        .orElseThrow(() -> new ResourceNotFoundException("Train not found with ID: " + ticket.getServiceId()));
//                if (train.getAvailableSeats() < ticket.getQuantity())
//                    throw new BadRequestException("Not enough seats available for Train ID: " + ticket.getServiceId());
//                train.setAvailableSeats(train.getAvailableSeats() - ticket.getQuantity());
//                trainRepository.save(train);
//            }
//            case "flight" -> {
//                Flight flight = flightRepository.findById(Long.parseLong(ticket.getServiceId()))
//                        .orElseThrow(() -> new ResourceNotFoundException("Flight not found with ID: " + ticket.getServiceId()));
//                if (flight.getAvailableSeats() < ticket.getQuantity())
//                    throw new BadRequestException("Not enough seats available for Flight ID: " + ticket.getServiceId());
//                flight.setAvailableSeats(flight.getAvailableSeats() - ticket.getQuantity());
//                flightRepository.save(flight);
//            }
//            case "movie" -> {
//                Movie movie = movieRepository.findById(Long.parseLong(ticket.getServiceId()))
//                        .orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + ticket.getServiceId()));
//                if (movie.getAvailableSeats() < ticket.getQuantity())
//                    throw new BadRequestException("Not enough seats available for Movie ID: " + ticket.getServiceId());
//                movie.setAvailableSeats(movie.getAvailableSeats() - ticket.getQuantity());
//                movieRepository.save(movie);
//            }
//            case "event" -> {
//                Event event = eventRepository.findById(Long.parseLong(ticket.getServiceId()))
//                        .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + ticket.getServiceId()));
//                if (event.getCapacity() < ticket.getQuantity())
//                    throw new BadRequestException("Not enough seats available for Event ID: " + ticket.getServiceId());
//                event.setCapacity(event.getCapacity() - ticket.getQuantity());
//                eventRepository.save(event);
//            }
//            default -> throw new BadRequestException("Invalid service type: " + ticket.getServiceType());
//        }
//
//        ticket.setBookingDate(LocalDateTime.now());
//        ticket.setPaymentStatus("PENDING");
//        return ticketRepository.save(ticket);
//    }
//
//    public String createPaymentOrder(Ticket ticket) throws RazorpayException {
//        RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
//        JSONObject orderRequest = new JSONObject();
//        orderRequest.put("amount", (int) (ticket.getTotalPrice() * 100)); // amount in paise
//        orderRequest.put("currency", "INR");
//        orderRequest.put("receipt", "order_rcptid_" + ticket.getId());
//        Order order = razorpay.orders.create(orderRequest);
//        return order.get("id");
//    }
//
//    public void updateTicketPaymentStatus(Long ticketId, String paymentId, String status) {
//        Ticket ticket = ticketRepository.findById(ticketId)
//                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + ticketId));
//        ticket.setPaymentStatus(status);
//        ticketRepository.save(ticket);
//    }
//
//    public List<TicketDetailsDto> getTicketsByUserId(String userId) {
//        String currentUserId = getCurrentUserId();
//        if (!currentUserId.equals(userId)) {
//            throw new SecurityException("Access denied: Cannot view tickets for another user.");
//        }
//        List<Ticket> tickets = ticketRepository.findByUserId(userId);
//        return tickets.stream().map(this::convertToDtoWithDetails).collect(Collectors.toList());
//    }
//
//    private TicketDetailsDto convertToDtoWithDetails(Ticket ticket) {
//        TicketDetailsDto dto = new TicketDetailsDto();
//        dto.setId(ticket.getId());
//        dto.setServiceType(ticket.getServiceType());
//        dto.setServiceId(ticket.getServiceId());
//        dto.setQuantity(ticket.getQuantity());
//        dto.setTotalPrice(ticket.getTotalPrice());
//        dto.setPaymentStatus(ticket.getPaymentStatus());
//        dto.setBookingDate(ticket.getBookingDate());
//
//        switch (ticket.getServiceType().toLowerCase()) {
//            case "bus" -> busRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(bus -> {
//                dto.setServiceName(bus.getOperator() + " Bus");
//                dto.setServiceRoute(bus.getDeparture() + " to " + bus.getDestination());
//                dto.setServiceTime(bus.getDepartureTime());
//            });
//            case "train" -> trainRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(train -> {
//                dto.setServiceName("Train " + train.getTrainNumber());
//                dto.setServiceRoute(train.getDeparture() + " to " + train.getDestination());
//                dto.setServiceTime(train.getDepartureTime());
//            });
//            case "flight" -> flightRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(flight -> {
//                dto.setServiceName(flight.getAirline() + " Flight " + flight.getFlightNumber());
//                dto.setServiceRoute(flight.getDeparture() + " to " + flight.getDestination());
//                dto.setServiceTime(flight.getDepartureTime());
//            });
//            case "movie" -> movieRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(movie -> {
//                dto.setServiceName(movie.getTitle());
//                dto.setServiceRoute("Theater: " + movie.getTheater());
//                dto.setServiceTime("Showtime: " + movie.getShowTime());
//            });
//            case "event" -> eventRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(event -> {
//                dto.setServiceName(event.getName());
//                dto.setServiceRoute("Location: " + event.getLocation());
//                dto.setServiceTime("Date/Time: " + event.getStartTime()); // ✅ FIXED FIELD
//            });
//        }
//
//        return dto;
//    }
//
//	public String createPaymentOrder(Long ticketId, Double totalPrice) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//}





//package com.ticketsystem.service;
//
//import com.razorpay.Order;
//import com.razorpay.RazorpayClient;
//import com.razorpay.RazorpayException;
//import com.ticketsystem.exception.BadRequestException;
//import com.ticketsystem.exception.ResourceNotFoundException;
//import com.ticketsystem.model.*;
//import com.ticketsystem.payload.TicketDetailsDto; // NEW IMPORT
//import com.ticketsystem.repository.*;
//import com.ticketsystem.security.UserDetailsImpl; // NEW IMPORT
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.*;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors; // NEW IMPORT
//
//@Service
//public class TicketService {
//
//    @Autowired private TicketRepository ticketRepository;
//    @Autowired private BusRepository busRepository;
//    @Autowired private TrainRepository trainRepository;
//    @Autowired private FlightRepository flightRepository;
//    @Autowired private MovieRepository movieRepository;
//    @Autowired private EventRepository eventRepository;
//    @Autowired private UserRepository userRepository;
//
//    @Value("${razorpay.key.id}")
//    private String razorpayKeyId;
//
//    @Value("${razorpay.key.secret}")
//    private String razorpayKeySecret;
//
//    // Helper to get current authenticated user's ID
//    private String getCurrentUserId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetailsImpl) {
//            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//            return userDetails.getId().toString(); // Directly get ID from UserDetailsImpl
//        }
//        throw new IllegalStateException("User not authenticated or principal is anonymous/invalid.");
//    }
//
//    public Ticket bookTicket(Ticket ticket) {
//        // Set userId from authenticated context
//        ticket.setUserId(getCurrentUserId());
//
//        switch (ticket.getServiceType().toLowerCase()) {
//            case "bus" -> {
//                Bus bus = busRepository.findById(Long.parseLong(ticket.getServiceId()))
//                        .orElseThrow(() -> new ResourceNotFoundException("Bus not found with ID: " + ticket.getServiceId()));
//                if (bus.getAvailableSeats() < ticket.getQuantity())
//                    throw new BadRequestException("Not enough seats available for Bus ID: " + ticket.getServiceId());
//                bus.setAvailableSeats(bus.getAvailableSeats() - ticket.getQuantity());
//                busRepository.save(bus);
//            }
//            case "train" -> {
//                Train train = trainRepository.findById(Long.parseLong(ticket.getServiceId()))
//                        .orElseThrow(() -> new ResourceNotFoundException("Train not found with ID: " + ticket.getServiceId()));
//                if (train.getAvailableSeats() < ticket.getQuantity())
//                    throw new BadRequestException("Not enough seats available for Train ID: " + ticket.getServiceId());
//                train.setAvailableSeats(train.getAvailableSeats() - ticket.getQuantity());
//                trainRepository.save(train);
//            }
//            case "flight" -> {
//                Flight flight = flightRepository.findById(Long.parseLong(ticket.getServiceId()))
//                        .orElseThrow(() -> new ResourceNotFoundException("Flight not found with ID: " + ticket.getServiceId()));
//                if (flight.getAvailableSeats() < ticket.getQuantity())
//                    throw new BadRequestException("Not enough seats available for Flight ID: " + ticket.getServiceId());
//                flight.setAvailableSeats(flight.getAvailableSeats() - ticket.getQuantity());
//                flightRepository.save(flight);
//            }
//            case "movie" -> {
//                Movie movie = movieRepository.findById(Long.parseLong(ticket.getServiceId()))
//                        .orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + ticket.getServiceId()));
//                if (movie.getAvailableSeats() < ticket.getQuantity())
//                    throw new BadRequestException("Not enough seats available for Movie ID: " + ticket.getServiceId());
//                movie.setAvailableSeats(movie.getAvailableSeats() - ticket.getQuantity());
//                movieRepository.save(movie);
//            }
//            case "event" -> {
//                Event event = eventRepository.findById(Long.parseLong(ticket.getServiceId()))
//                        .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + ticket.getServiceId()));
//                if (event.getAvailableSeats() < ticket.getQuantity())
//                    throw new BadRequestException("Not enough seats available for Event ID: " + ticket.getServiceId());
//                event.setAvailableSeats(event.getAvailableSeats() - ticket.getQuantity());
//                eventRepository.save(event);
//            }
//            default -> throw new BadRequestException("Invalid service type: " + ticket.getServiceType());
//        }
//
//        ticket.setBookingDate(LocalDateTime.now());
//        ticket.setPaymentStatus("PENDING");
//        return ticketRepository.save(ticket);
//    }
//
//    public String createPaymentOrder(Ticket ticket) throws RazorpayException {
//        RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
//        JSONObject orderRequest = new JSONObject();
//        orderRequest.put("amount", (int) (ticket.getTotalPrice() * 100)); // amount in smallest currency unit
//        orderRequest.put("currency", "INR");
//        orderRequest.put("receipt", "order_rcptid_" + ticket.getId());
//        Order order = razorpay.orders.create(orderRequest);
//        return order.get("id"); // Return the order ID for frontend to initiate payment
//    }
//
//    // NEW METHOD: Update Ticket Payment Status
//    public void updateTicketPaymentStatus(Long ticketId, String paymentId, String status) {
//        Ticket ticket = ticketRepository.findById(ticketId)
//                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + ticketId));
//        ticket.setPaymentStatus(status);
//        // You might want to add a 'razorpayPaymentId' field to your Ticket model
//        // ticket.setRazorpayPaymentId(paymentId);
//        ticketRepository.save(ticket);
//    }
//
//    // MODIFIED METHOD: getTicketsByUserId to return DTOs
//    public List<TicketDetailsDto> getTicketsByUserId(String userId) {
//        String currentUserId = getCurrentUserId();
//        if (!currentUserId.equals(userId)) {
//            throw new SecurityException("Access denied: Cannot view tickets for another user.");
//        }
//        List<Ticket> tickets = ticketRepository.findByUserId(userId);
//        return tickets.stream().map(this::convertToDtoWithDetails).collect(Collectors.toList());
//    }
//
//    // NEW HELPER METHOD: Convert Ticket to TicketDetailsDto with enriched details
//    private TicketDetailsDto convertToDtoWithDetails(Ticket ticket) {
//        TicketDetailsDto dto = new TicketDetailsDto();
//        dto.setId(ticket.getId());
//        dto.setServiceType(ticket.getServiceType());
//        dto.setServiceId(ticket.getServiceId());
//        dto.setQuantity(ticket.getQuantity());
//        dto.setTotalPrice(ticket.getTotalPrice());
//        dto.setPaymentStatus(ticket.getPaymentStatus());
//        dto.setBookingDate(ticket.getBookingDate());
//
//        // Fetch and set specific service details based on serviceType
//        switch (ticket.getServiceType().toLowerCase()) {
//            case "bus" -> busRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(bus -> {
//                dto.setServiceName(bus.getOperator() + " Bus");
//                dto.setServiceRoute(bus.getDeparture() + " to " + bus.getDestination());
//                dto.setServiceTime(bus.getDepartureTime());
//            });
//            case "train" -> trainRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(train -> {
//                dto.setServiceName("Train " + train.getTrainNumber());
//                dto.setServiceRoute(train.getDeparture() + " to " + train.getDestination());
//                dto.setServiceTime(train.getDepartureTime());
//            });
//            case "flight" -> flightRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(flight -> {
//                dto.setServiceName(flight.getAirline() + " Flight " + flight.getFlightNumber());
//                dto.setServiceRoute(flight.getDeparture() + " to " + flight.getDestination());
//                dto.setServiceTime(flight.getDepartureTime());
//            });
//            case "movie" -> movieRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(movie -> {
//                dto.setServiceName(movie.getTitle());
//                dto.setServiceRoute("Theater: " + movie.getTheater());
//                dto.setServiceTime("Showtime: " + movie.getShowTime());
//            });
//            case "event" -> eventRepository.findById(Long.parseLong(ticket.getServiceId())).ifPresent(event -> {
//                dto.setServiceName(event.getName());
//                dto.setServiceRoute("Location: " + event.getLocation());
//                dto.setServiceTime("Date/Time: " + event.getDateTime());
//            });
//        }
//        return dto;
//    }
//}
