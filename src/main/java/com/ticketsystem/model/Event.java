package com.ticketsystem.model;

import java.util.Objects;
import jakarta.persistence.*;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;
    private String startTime;
    private int capacity;
    private double price;

    public Event() {}

    public Event(String name, String location, String startTime, int capacity, double price) {
        this.name = name;
        this.location = location;
        this.startTime = startTime;
        this.capacity = capacity;
        this.price = price;
    }

    public Event(Long id, String name, String location, String startTime, int capacity, double price) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.startTime = startTime;
        this.capacity = capacity;
        this.price = price;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return capacity == event.capacity &&
               Double.compare(price, event.price) == 0 &&
               Objects.equals(id, event.id) &&
               Objects.equals(name, event.name) &&
               Objects.equals(location, event.location) &&
               Objects.equals(startTime, event.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, location, startTime, capacity, price);
    }
}





//package com.ticketsystem.model;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "events")
//public class Event {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String name;
//    private String location;
//    private String dateTime;
//    private int availableSeats;
//    private double price;
//	public Long getId() {
//		return id;
//	}
//	public void setId(Long id) {
//		this.id = id;
//	}
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
//	public String getLocation() {
//		return location;
//	}
//	public void setLocation(String location) {
//		this.location = location;
//	}
//	public String getDateTime() {
//		return dateTime;
//	}
//	public void setDateTime(String dateTime) {
//		this.dateTime = dateTime;
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
//	public Event(Long id, String name, String location, String dateTime, int availableSeats, double price) {
//		super();
//		this.id = id;
//		this.name = name;
//		this.location = location;
//		this.dateTime = dateTime;
//		this.availableSeats = availableSeats;
//		this.price = price;
//	}
//	public Event() {
//		super();
//		// TODO Auto-generated constructor stub
//	}
//
//    // Getters and Setters
//}
