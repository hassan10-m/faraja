package tz.faraja.repository;
import tz.faraja.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
public interface InvoiceRepository extends JpaRepository<Invoice,Long>  {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE) @org.springframework.data.jpa.repository.Query("select x from Invoice x where x.id = :id") java.util.Optional<Invoice> lockById(@org.springframework.data.repository.query.Param("id") Long id);
}
