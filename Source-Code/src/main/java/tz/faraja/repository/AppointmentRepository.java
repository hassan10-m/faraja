package tz.faraja.repository;
import tz.faraja.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AppointmentRepository extends JpaRepository<Appointment,Long>  {
}
