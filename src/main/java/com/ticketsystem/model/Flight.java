package com.ticketsystem.model;

import java.util.Objects;
import jakarta.persistence.*;

@Entity
@Table(name = "flights")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String flightNumber;
    private String airline;
    private String departure;
    private String destination;
    private String departureTime;
    private int availableSeats;
    private double price;

    // No-arg constructor
    public Flight() {}

    // Constructor without ID
    public Flight(String flightNumber, String airline, String departure, String destination, String departureTime,
                  int availableSeats, double price) {
        this.flightNumber = flightNumber;
        this.airline = airline;
        this.departure = departure;
        this.destination = destination;
        this.departureTime = departureTime;
        this.availableSeats = availableSeats;
        this.price = price;
    }

    // Full constructor with ID
    public Flight(Long id, String flightNumber, String airline, String departure, String destination, String departureTime,
                  int availableSeats, double price) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.airline = airline;
        this.departure = departure;
        this.destination = destination;
        this.departureTime = departureTime;
        this.availableSeats = availableSeats;
        this.price = price;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getAirline() { return airline; }
    public void setAirline(String airline) { this.airline = airline; }

    public String getDeparture() { return departure; }
    public void setDeparture(String departure) { this.departure = departure; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Flight)) return false;
        Flight flight = (Flight) o;
        return availableSeats == flight.availableSeats &&
               Double.compare(price, flight.price) == 0 &&
               Objects.equals(id, flight.id) &&
               Objects.equals(flightNumber, flight.flightNumber) &&
               Objects.equals(airline, flight.airline) &&
               Objects.equals(departure, flight.departure) &&
               Objects.equals(destination, flight.destination) &&
               Objects.equals(departureTime, flight.departureTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, flightNumber, airline, departure, destination, departureTime, availableSeats, price);
    }
}




//package com.ticketsystem.model;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "flights")
//public class Flight {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String flightNumber;
//    private String airline;
//    private String departure;
//    private String destination;
//    private String departureTime;
//    private int availableSeats;
//    private double price;
//	public Long getId() {
//		return id;
//	}
//	public void setId(Long id) {
//		this.id = id;
//	}
//	public String getFlightNumber() {
//		return flightNumber;
//	}
//	public void setFlightNumber(String flightNumber) {
//		this.flightNumber = flightNumber;
//	}
//	public String getAirline() {
//		return airline;
//	}
//	public void setAirline(String airline) {
//		this.airline = airline;
//	}
//	public String getDeparture() {
//		return departure;
//	}
//	public void setDeparture(String departure) {
//		this.departure = departure;
//	}
//	public String getDestination() {
//		return destination;
//	}
//	public void setDestination(String destination) {
//		this.destination = destination;
//	}
//	public String getDepartureTime() {
//		return departureTime;
//	}
//	public void setDepartureTime(String departureTime) {
//		this.departureTime = departureTime;
//	}
//	public int getAvailableSeats() {
//		return availableSeats;
//	}
//	public void setAvailableSeats(int availableSeats) {
//		this.availableSeats = availableSeats;
//	}
//	public double getPrice() {
//		return price;
//	}
//	public void setPrice(double price) {
//		this.price = price;
//	}
//	public Flight(Long id, String flightNumber, String airline, String departure, String destination,
//			String departureTime, int availableSeats, double price) {
//		super();
//		this.id = id;
//		this.flightNumber = flightNumber;
//		this.airline = airline;
//		this.departure = departure;
//		this.destination = destination;
//		this.departureTime = departureTime;
//		this.availableSeats = availableSeats;
//		this.price = price;
//	}
//	public Flight() {
//		super();
//		// TODO Auto-generated constructor stub
//	}
//
//    // Getters and Setters
//}
