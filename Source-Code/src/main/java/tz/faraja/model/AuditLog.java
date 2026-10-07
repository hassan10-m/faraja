package tz.faraja.model;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.*;
import java.math.*;
@Entity @Table(name="audit_logs") public class AuditLog  {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(name="actor") public String actor;
    @Column(name="action") public String action;
    @Column(name="target") public String target;
    @Column(name="created_at") public LocalDateTime createdAt;
    @PrePersist public void create() {
        createdAt=LocalDateTime.now();
    }
}
