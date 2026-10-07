package tz.faraja.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.*;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import tz.faraja.model.User;
import tz.faraja.repository.UserRepository;
@Configuration public class Bootstrap  {
    @Bean ApplicationRunner admin(UserRepository users,PasswordEncoder encoder,@Value("${faraja.admin.username}") String username,@Value("${faraja.admin.password}") String password) {
        return args-> {
            if(users.count()==0) {
                if(password.isBlank()||password.length()<12)throw new IllegalStateException("Set ADMIN_PASSWORD to at least 12 characters before first start");
                User u=new User();
                u.username=username;
                u.password=encoder.encode(password);
                u.fullName="System Administrator";
                u.role="ADMIN";
                u.active=true;
                users.save(u);
            }
        }
        ;
    }
}
