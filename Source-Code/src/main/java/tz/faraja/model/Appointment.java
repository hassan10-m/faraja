package tz.faraja.model;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.*;
import java.math.*;
@Entity @Table(name="appointments") public class Appointment  {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="patient_id") public Patient patient;
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="doctor_id") public User doctor;
    @Column(name="scheduled_at") public LocalDateTime scheduledAt;
    @Column(name="reason") public String reason;
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
