package tz.faraja.repository;
import tz.faraja.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PaymentRepository extends JpaRepository<Payment,Long>  {
}
