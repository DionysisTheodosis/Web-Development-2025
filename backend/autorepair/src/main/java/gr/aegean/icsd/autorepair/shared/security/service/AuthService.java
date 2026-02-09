package gr.aegean.icsd.autorepair.shared.security.service;

import gr.aegean.icsd.autorepair.shared.security.CustomUserDetails;
import gr.aegean.icsd.autorepair.shared.security.InvalidCredentialsException;
import gr.aegean.icsd.autorepair.shared.security.LoginRequestDto;
import gr.aegean.icsd.autorepair.shared.utils.AuthUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final MyUserDetailsService userDetailsService;
    private final AuthUtils authUtils;

    public String authenticateAndSetToken(LoginRequestDto dto, HttpServletResponse response) {

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
        );

        if (!authentication.isAuthenticated()) {
            throw new InvalidCredentialsException();
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(dto.username());
        String token = jwtService.generateToken(userDetails.getUsername());
        jwtService.attachJwtCookie(response, token);
        return "Authentication successful.";
    }

    public String getAuthenticatedUsername() {
        return authUtils.getAuthenticatedUsername();
    }

    public void revokeToken(HttpServletResponse response) {
        jwtService.clearJwtCookie(response);
    }


}
