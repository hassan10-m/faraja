package tz.faraja.model;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.*;
import java.math.*;
@Entity @Table(name="prescriptions") public class Prescription  {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="visit_id") public Visit visit;
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="medicine_id") public Medicine medicine;
    @Column(name="quantity") public int quantity;
    @Column(name="instructions") public String instructions;
    @Column(name="dispensed") public boolean dispensed;
    @Column(name="created_at") public LocalDateTime createdAt;
    @Column(name="updated_at") public LocalDateTime updatedAt;
    @PrePersist public void create() {
        createdAt=LocalDateTime.now();
        updatedAt=createdAt;
    }
    @PreUpdate public void update() {
        updatedAt=LocalDateTime.now();
    }
}
