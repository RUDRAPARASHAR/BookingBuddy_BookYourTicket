// Role.java
package com.ticketsystem.model;
import jakarta.persistence.*;
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;
    public Role() {}
    public Role(ERole name) {
        this.name = name;
    }
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ERole getName() { return name; }
    public void setName(ERole name) { this.name = name; }
}


//package com.ticketsystem.model;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "roles")
//public class Role {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
//
//    @Enumerated(EnumType.STRING)
//    @Column(length = 20)
//    private ERole name;
//
//    public Role() {}
//
//    public Role(ERole name) {
//        this.name = name;
//    }
//
//	public Integer getId() {
//		return id;
//	}
//
//	public void setId(Integer id) {
//		this.id = id;
//	}
//
//	public ERole getName() {
//		return name;
//	}
//
//	public void setName(ERole name) {
//		this.name = name;
//	}
//
//    // Getters and Setters
//}
