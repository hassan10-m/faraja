package tz.faraja.model;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.*;
import java.math.*;
@Entity @Table(name="medicines") public class Medicine  {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(name="name", unique=true) @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max=120) public String name;
    @Column(name="unit") @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max=30) public String unit;
    @Column(name="stock") @jakarta.validation.constraints.Min(0) public int stock;
    @Column(name="reorder_level") @jakarta.validation.constraints.Min(0) public int reorderLevel;
    @Column(name="unit_price") @jakarta.validation.constraints.NotNull @jakarta.validation.constraints.DecimalMin("0.00") @jakarta.validation.constraints.Digits(integer=10,fraction=2) public BigDecimal unitPrice;
    @Column(name="expiry_date") public LocalDate expiryDate;
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
