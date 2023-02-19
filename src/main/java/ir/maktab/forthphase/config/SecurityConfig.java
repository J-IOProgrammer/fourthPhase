package ir.maktab.forthphase.config;

import ir.maktab.forthphase.data.repository.CustomerRepository;
import ir.maktab.forthphase.data.repository.ExpertRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final ExpertRepository expertRepository;
    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public SecurityConfig(ExpertRepository expertRepository,
                          CustomerRepository customerRepository,
                          BCryptPasswordEncoder passwordEncoder) {
        this.expertRepository = expertRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/expert/register").permitAll()
                .requestMatchers("/customer/register").permitAll()
//                .requestMatchers("/expert/**").hasRole("EXPERT")
//                .requestMatchers("/admin/**").hasRole("ADMIN")
//                .requestMatchers("/customer/**").hasRole("CUSTOMER")

//                .requestMatchers("/service/saveService").hasRole("ADMIN")
//                .requestMatchers("/service/findAllService").hasAnyRole("CUSTOMER","ADMIN")
//                .requestMatchers("/service/editService").hasAnyRole("ADMIN", "EXPERT")
                .anyRequest().authenticated().and().httpBasic();
        return http.build();
    }
//
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth)
//            throws Exception {
//        auth.userDetailsService((username) -> userRepository
//                        .findByUsername(username)
//                        .orElseThrow(() -> new UsernameNotFoundException(String.format("This %s notFound!", username))))
//                .passwordEncoder(passwordEncoder);

//        .userDetailsService(username -> userRepository
//                .findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException(String
//                        .format("this %s not found", username))))
//                .passwordEncoder(passwordEncoder).and()
//
//                .userDetailsService(username -> adminRepository
//                        .findByUsername(username)
//                        .orElseThrow(() -> new UsernameNotFoundException(String
//                                .format("this %s not found", username))))
//                .passwordEncoder(passwordEncoder).and()
//
//                .userDetailsService(username -> expertRepository
//                        .findByUsername(username)
//                        .orElseThrow(() -> new UsernameNotFoundException(String
//                                .format("this %s not found", username))))
//                .passwordEncoder(passwordEncoder);
    }


