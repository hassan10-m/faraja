package tz.faraja.model;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.*;
import java.math.*;
@Entity @Table(name="payments") public class Payment  {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="invoice_id") public Invoice invoice;
    @Column(name="amount") public BigDecimal amount;
    @Column(name="method") public String method;
    @Column(name="reference") public String reference;
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
