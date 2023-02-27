package ir.maktab.forthphase.config;

import ir.maktab.forthphase.data.repository.AdminRepository;
import ir.maktab.forthphase.data.repository.CustomerRepository;
import ir.maktab.forthphase.data.repository.ExpertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final AdminRepository adminRepository;
    private final CustomerRepository customerRepository;
    private final ExpertRepository expertRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public SecurityConfig(AdminRepository adminRepository, CustomerRepository customerRepository,
                          ExpertRepository expertRepository, BCryptPasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.customerRepository = customerRepository;
        this.expertRepository = expertRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/customer/new", "/expert/new",
                        "/expert/verify/**", "/expert/confirm_verifying/**"
                , "/verify/**")
                .permitAll()
                .requestMatchers("/expert/**").hasAnyRole("EXPERT", "ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/customer/**").hasAnyRole("CUSTOMER", "ADMIN")
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();
        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception {

        auth.userDetailsService(username -> adminRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException(String
                                .format("this %s not found", username))))
                .passwordEncoder(passwordEncoder).and()

                .userDetailsService(username -> customerRepository
                        .findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException(String
                                .format("this %s not found", username))))
                .passwordEncoder(passwordEncoder).and()

                .userDetailsService(username -> expertRepository
                        .findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException(String
                                .format("this %s not found", username))))
                .passwordEncoder(passwordEncoder);
    }
}


