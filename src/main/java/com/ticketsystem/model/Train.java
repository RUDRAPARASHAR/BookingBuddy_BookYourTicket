package com.ticketsystem.model;

import java.util.Objects;
import jakarta.persistence.*;

@Entity
@Table(name = "trains")
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trainNumber;
    private String departure;
    private String destination;
    private String departureTime;
    private int availableSeats;
    private double price;

    // ✅ No-arg constructor
    public Train() {
        super();
    }

    // ✅ Constructor without ID
    public Train(String trainNumber, String departure, String destination, String departureTime, int availableSeats, double price) {
        this.trainNumber = trainNumber;
        this.departure = departure;
        this.destination = destination;
        this.departureTime = departureTime;
        this.availableSeats = availableSeats;
        this.price = price;
    }

    // ✅ Full-arg constructor including ID
    public Train(Long id, String trainNumber, String departure, String destination, String departureTime, int availableSeats, double price) {
        this.id = id;
        this.trainNumber = trainNumber;
        this.departure = departure;
        this.destination = destination;
        this.departureTime = departureTime;
        this.availableSeats = availableSeats;
        this.price = price;
    }

    // ✅ Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // ✅ equals and hashCode (optional but recommended)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Train)) return false;
        Train train = (Train) o;
        return availableSeats == train.availableSeats &&
               Double.compare(price, train.price) == 0 &&
               Objects.equals(id, train.id) &&
               Objects.equals(trainNumber, train.trainNumber) &&
               Objects.equals(departure, train.departure) &&
               Objects.equals(destination, train.destination) &&
               Objects.equals(departureTime, train.departureTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, trainNumber, departure, destination, departureTime, availableSeats, price);
    }
}




//package com.ticketsystem.model;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "trains")
//public class Train {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String trainNumber;
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
//	public String getTrainNumber() {
//		return trainNumber;
//	}
//	public void setTrainNumber(String trainNumber) {
//		this.trainNumber = trainNumber;
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
//
//    // Getters and Setters
//}
