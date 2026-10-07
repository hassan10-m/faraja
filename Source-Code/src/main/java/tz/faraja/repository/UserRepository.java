package tz.faraja.repository;
import tz.faraja.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User,Long>  {
    java.util.Optional<User> findByUsername(String username);
}
