package tz.faraja.repository;
import tz.faraja.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PrescriptionRepository extends JpaRepository<Prescription,Long>  {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE) @org.springframework.data.jpa.repository.Query("select x from Prescription x where x.id = :id") java.util.Optional<Prescription> lockById(@org.springframework.data.repository.query.Param("id") Long id);
}
