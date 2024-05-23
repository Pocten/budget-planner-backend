package cz.cvut.fel.budgetplannerbackend.config;

import cz.cvut.fel.budgetplannerbackend.security.jwt.JwtAuthenticationFilter;
import cz.cvut.fel.budgetplannerbackend.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

/**
 * Spring Security configuration class.
 * Configures authentication and authorization rules for the application.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService; // Service for loading user details during authentication.

    /**
     * Creates a BCryptPasswordEncoder bean for password hashing.
     *
     * @return A BCryptPasswordEncoder instance.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain for the application.
     *
     * @param http         The HttpSecurity object used to configure security settings.
     * @param tokenProvider The JwtTokenProvider used for JWT authentication.
     * @return The configured SecurityFilterChain.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenProvider tokenProvider) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection.
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // Allow unrestricted access to Swagger UI.
                        .requestMatchers("/", "/home", "/registration", "/api/auth/**").permitAll() // Allow unrestricted access to home, registration, and authentication endpoints.
                        .requestMatchers("/api/v1/dashboards/*/invite-links/use/**").permitAll() // Allow unrestricted access to invite link usage endpoints.
                        .anyRequest().authenticated() // All other requests require authentication.
                )
                // Add the JWT authentication filter before the UsernamePasswordAuthenticationFilter.
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider, userDetailsService), UsernamePasswordAuthenticationFilter.class)
                .cors(Customizer.withDefaults()); // Enable CORS with default configuration.

        return http.build(); // Build and return the SecurityFilterChain.
    }

    /**
     * Creates an AuthenticationManager bean.
     *
     * @return The configured AuthenticationManager.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        // Create a ProviderManager with a list of AuthenticationProviders.
        return new ProviderManager(Arrays.asList(daoAuthenticationProvider()));
    }

    /**
     * Creates a DaoAuthenticationProvider bean for user authentication.
     *
     * @return The configured DaoAuthenticationProvider.
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder()); // Set the password encoder for hashing passwords.
        provider.setUserDetailsService(userDetailsService); // Set the UserDetailsService to load user details.
        return provider; // Return the configured provider.
    }
}