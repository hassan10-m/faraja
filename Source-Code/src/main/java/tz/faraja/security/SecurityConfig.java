package tz.faraja.security;
import org.springframework.context.annotation.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import tz.faraja.repository.UserRepository;
@Configuration public class SecurityConfig  {
    @Bean PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean UserDetailsService users(UserRepository repository) {
        return username -> repository.findByUsername(username).filter(u->u.active).map(u->User.withUsername(u.username).password(u.password).roles(u.role).build()).orElseThrow(()->new UsernameNotFoundException(username));
    }
    @Bean SecurityFilterChain filter(HttpSecurity http)throws Exception  {
        return http .authorizeHttpRequests(a->a.requestMatchers("/login.html","/css/**","/js/**","/error","/api/csrf").permitAll().requestMatchers("/api/admin/**").hasRole("ADMIN").requestMatchers("/api/reports/**").hasAnyRole("ADMIN","CASHIER").anyRequest().authenticated()) .formLogin(f->f.loginPage("/login.html").loginProcessingUrl("/login").defaultSuccessUrl("/index.html",true).failureUrl("/login.html?error")) .logout(l->l.logoutSuccessUrl("/login.html")) .csrf(c->c.csrfTokenRepository(org.springframework.security.web.csrf.CookieCsrfTokenRepository.withHttpOnlyFalse())) .build();
    }
}
