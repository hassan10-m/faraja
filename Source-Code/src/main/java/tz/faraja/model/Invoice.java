package tz.faraja.model;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.*;
import java.math.*;
@Entity @Table(name="invoices") public class Invoice  {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @OneToOne @JoinColumn(name="visit_id") public Visit visit;
    @Column(name="amount") public BigDecimal amount;
    @Column(name="description") public String description;
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
