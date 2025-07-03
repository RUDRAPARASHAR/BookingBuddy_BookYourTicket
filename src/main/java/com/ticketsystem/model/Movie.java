package com.ticketsystem.model;

import java.util.Objects;
import jakarta.persistence.*;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String theater;
    private String showTime;
    private int availableSeats;
    private double price;

    public Movie() {}

    public Movie(String title, String theater, String showTime, int availableSeats, double price) {
        this.title = title;
        this.theater = theater;
        this.showTime = showTime;
        this.availableSeats = availableSeats;
        this.price = price;
    }

    public Movie(Long id, String title, String theater, String showTime, int availableSeats, double price) {
        this.id = id;
        this.title = title;
        this.theater = theater;
        this.showTime = showTime;
        this.availableSeats = availableSeats;
        this.price = price;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTheater() { return theater; }
    public void setTheater(String theater) { this.theater = theater; }

    public String getShowTime() { return showTime; }
    public void setShowTime(String showTime) { this.showTime = showTime; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie)) return false;
        Movie movie = (Movie) o;
        return availableSeats == movie.availableSeats &&
               Double.compare(price, movie.price) == 0 &&
               Objects.equals(id, movie.id) &&
               Objects.equals(title, movie.title) &&
               Objects.equals(theater, movie.theater) &&
               Objects.equals(showTime, movie.showTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, theater, showTime, availableSeats, price);
    }
}




//package com.ticketsystem.model;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "movies")
//public class Movie {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String title;
//    private String theater;
//    private String showTime;
//    private int availableSeats;
//    private double ticketPrice;
//	public Long getId() {
//		return id;
//	}
//	public void setId(Long id) {
//		this.id = id;
//	}
//	public String getTitle() {
//		return title;
//	}
//	public void setTitle(String title) {
//		this.title = title;
//	}
//	public String getTheater() {
//		return theater;
//	}
//	public void setTheater(String theater) {
//		this.theater = theater;
//	}
//	public String getShowTime() {
//		return showTime;
//	}
//	public void setShowTime(String showTime) {
//		this.showTime = showTime;
//	}
//	public int getAvailableSeats() {
//		return availableSeats;
//	}
//	public void setAvailableSeats(int availableSeats) {
//		this.availableSeats = availableSeats;
//	}
//	public double getTicketPrice() {
//		return ticketPrice;
//	}
//	public void setTicketPrice(double ticketPrice) {
//		this.ticketPrice = ticketPrice;
//	}
//	public Movie(Long id, String title, String theater, String showTime, int availableSeats, double ticketPrice) {
//		super();
//		this.id = id;
//		this.title = title;
//		this.theater = theater;
//		this.showTime = showTime;
//		this.availableSeats = availableSeats;
//		this.ticketPrice = ticketPrice;
//	}
//	public Movie() {
//		super();
//		// TODO Auto-generated constructor stub
//	}
//
//    // Getters and Setters
//}
