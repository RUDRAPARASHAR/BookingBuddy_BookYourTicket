package com.ticketsystem.model;

import java.util.Objects;
import jakarta.persistence.*;

@Entity
@Table(name = "buses")
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String departure;
    private String destination;
    private String operator;
    private String departureTime;
    private int availableSeats;
    private double price;

    // ✅ No-arg constructor
    public Bus() {
        super();
    }

    // ✅ Full-arg constructor including ID
    public Bus(Long id, String departure, String destination, String operator, String departureTime, int availableSeats,
               double price) {
        this.id = id;
        this.departure = departure;
        this.destination = destination;
        this.operator = operator;
        this.departureTime = departureTime;
        this.availableSeats = availableSeats;
        this.price = price;
    }

    // ✅ 6-arg constructor excluding ID (fixes your current error)
    public Bus(String departure, String destination, String operator, String departureTime, int availableSeats,
               double price) {
        this.departure = departure;
        this.destination = destination;
        this.operator = operator;
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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
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

    // ✅ Equals and HashCode
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Bus)) return false;
        Bus other = (Bus) obj;
        return availableSeats == other.availableSeats &&
               Double.compare(price, other.price) == 0 &&
               Objects.equals(id, other.id) &&
               Objects.equals(departure, other.departure) &&
               Objects.equals(destination, other.destination) &&
               Objects.equals(operator, other.operator) &&
               Objects.equals(departureTime, other.departureTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, departure, destination, operator, departureTime, availableSeats, price);
    }
}




//package com.ticketsystem.model;
//
//import java.util.Objects;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "buses")
//public class Bus {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String departure;
//    private String destination;
//    private String operator;
//    private String departureTime;
//    private int availableSeats;
//    private double price;
//	public Long getId() {
//		return id;
//	}
//	public void setId(Long id) {
//		this.id = id;
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
//	public String getOperator() {
//		return operator;
//	}
//	public void setOperator(String operator) {
//		this.operator = operator;
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
//	public Bus(Long id, String departure, String destination, String operator, String departureTime, int availableSeats,
//			double price) {
//		super();
//		this.id = id;
//		this.departure = departure;
//		this.destination = destination;
//		this.operator = operator;
//		this.departureTime = departureTime;
//		this.availableSeats = availableSeats;
//		this.price = price;
//	}
//	@Override
//	public int hashCode() {
//		return Objects.hash(availableSeats, departure, departureTime, destination, id, operator, price);
//	}
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		Bus other = (Bus) obj;
//		return availableSeats == other.availableSeats && Objects.equals(departure, other.departure)
//				&& Objects.equals(departureTime, other.departureTime) && Objects.equals(destination, other.destination)
//				&& Objects.equals(id, other.id) && Objects.equals(operator, other.operator)
//				&& Double.doubleToLongBits(price) == Double.doubleToLongBits(other.price);
//	}
//	public Bus() {
//		super();
//		// TODO Auto-generated constructor stub
//	}
//
//    // Getters and Setters
//}
