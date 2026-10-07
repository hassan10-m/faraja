package tz.faraja.model;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.*;
import java.math.*;
@Entity @Table(name="patients") public class Patient  {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(name="number", unique=true) public String number;
    @Column(name="full_name") @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max=120) public String fullName;
    @Column(name="birth_date") @jakarta.validation.constraints.PastOrPresent public LocalDate birthDate;
    @Column(name="sex") @jakarta.validation.constraints.Size(max=15) public String sex;
    @Column(name="phone") @jakarta.validation.constraints.Size(max=30) public String phone;
    @Column(name="address") @jakarta.validation.constraints.Size(max=255) public String address;
    @Column(name="allergies", columnDefinition="TEXT") public String allergies;
    @Column(name="created_at") public LocalDateTime createdAt;
    @Column(name="updated_at") public LocalDateTime updatedAt;
    @PrePersist public void create() {
        createdAt=LocalDateTime.now();
        updatedAt=createdAt;
    }
    @PreUpdate public void update() {
        updatedAt=LocalDateTime.now();
    }
    @JsonIgnore @OneToMany(mappedBy="patient") public java.util.List<Visit> visits = new java.util.ArrayList<>();
}
