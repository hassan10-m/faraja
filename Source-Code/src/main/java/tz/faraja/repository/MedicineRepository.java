package tz.faraja.repository;
import tz.faraja.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MedicineRepository extends JpaRepository<Medicine,Long>  {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE) @org.springframework.data.jpa.repository.Query("select x from Medicine x where x.id = :id") java.util.Optional<Medicine> lockById(@org.springframework.data.repository.query.Param("id") Long id);
    @org.springframework.data.jpa.repository.Modifying(flushAutomatically=true) @org.springframework.data.jpa.repository.Query("update Medicine m set m.stock=m.stock-:quantity where m.id=:id and m.stock>=:quantity and (m.expiryDate is null or m.expiryDate>:today)") int deductStock(@org.springframework.data.repository.query.Param("id") Long id,@org.springframework.data.repository.query.Param("quantity") int quantity,@org.springframework.data.repository.query.Param("today") java.time.LocalDate today);
}
