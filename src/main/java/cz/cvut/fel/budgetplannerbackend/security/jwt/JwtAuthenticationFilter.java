package cz.cvut.fel.budgetplannerbackend.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter responsible for intercepting incoming requests, extracting the JWT token from the Authorization header,
 * validating the token, and setting the authentication in the Spring Security context if the token is valid.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, UserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Performs the filtering logic for each incoming request.
     *
     * @param request The HttpServletRequest to filter.
     * @param response The HttpServletResponse to filter.
     * @param filterChain The FilterChain to continue processing the request.
     * @throws ServletException If a servlet-related error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Get the JWT token from the Authorization header of the request.
        String jwt = getJwtFromRequest(request);

        LOG.info("JWT from request: {}", jwt);

        // If a JWT token is present and valid:
        if (jwt != null && tokenProvider.validateToken(jwt)) {
            String username = tokenProvider.getUsernameFromJWT(jwt); // Extract the username from the JWT.
            LOG.info("Username from JWT: {}", username);

            // Load user details from the UserDetailsService using the extracted username.
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Create a UsernamePasswordAuthenticationToken using the loaded UserDetails.
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set the authentication in the Spring Security context.
            SecurityContextHolder.getContext().setAuthentication(authentication);
            LOG.info("Authentication set in security context for: {}", username);
        } else {
            // Log a warning if no valid JWT is found.
            LOG.warn("No valid JWT found, continuing filter chain without authentication");
        }

        // Continue processing the request through the filter chain.
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the Authorization header of the request.
     *
     * @param request The HttpServletRequest.
     * @return The JWT token string, or null if not found or invalid.
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) { // Check if the header starts with "Bearer ".
            return bearerToken.substring(7); // Extract the token from the header.
        }
        return null;
    }
}