package tz.faraja.repository;
import tz.faraja.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
public interface VisitRepository extends JpaRepository<Visit,Long>  {
    java.util.List<Visit> findByStatusOrderByCreatedAtAsc(String status);
    java.util.List<Visit> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}
