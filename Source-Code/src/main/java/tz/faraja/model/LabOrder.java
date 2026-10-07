package tz.faraja.model;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.*;
import java.math.*;
@Entity @Table(name="lab_orders") public class LabOrder  {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="visit_id") public Visit visit;
    @Column(name="test_name") public String testName;
    @Column(name="result_text", columnDefinition="TEXT") public String resultText;
    @Column(name="status") public String status;
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
