package tz.faraja.model;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.*;
import java.math.*;
@Entity @Table(name="visits") public class Visit  {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="patient_id") public Patient patient;
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="clinician_id") public User clinician;
    @OneToOne @JoinColumn(name="appointment_id") public Appointment appointment;
    @Column(name="status") public String status;
    @Column(name="complaint", columnDefinition="TEXT") public String complaint;
    @Column(name="history", columnDefinition="TEXT") public String history;
    @Column(name="examination", columnDefinition="TEXT") public String examination;
    @Column(name="diagnosis", columnDefinition="TEXT") public String diagnosis;
    @Column(name="treatment", columnDefinition="TEXT") public String treatment;
    @Column(name="systolic") public Integer systolic;
    @Column(name="diastolic") public Integer diastolic;
    @Column(name="pulse") public Integer pulse;
    @Column(name="temperature") public BigDecimal temperature;
    @Column(name="weight_kg") public BigDecimal weightKg;
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
