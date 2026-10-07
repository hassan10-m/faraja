package tz.faraja.repository;
import tz.faraja.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PatientRepository extends JpaRepository<Patient,Long>  {
}
