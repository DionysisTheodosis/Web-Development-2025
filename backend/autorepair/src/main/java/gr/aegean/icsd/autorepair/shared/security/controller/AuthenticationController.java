package gr.aegean.icsd.autorepair.shared.security.controller;


import gr.aegean.icsd.autorepair.shared.security.AuthInfoDto;
import gr.aegean.icsd.autorepair.shared.security.LoginRequestDto;
import gr.aegean.icsd.autorepair.shared.security.service.AuthService;
import gr.aegean.icsd.autorepair.user.UserRegistrationDto;
import gr.aegean.icsd.autorepair.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication")
public class AuthenticationController {
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequestDto dto, HttpServletResponse response) {
        return ResponseEntity.ok(authService.authenticateAndSetToken(dto, response));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid UserRegistrationDto dto) {
        userService.saveUser(dto);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        authService.revokeToken(response);
        return ResponseEntity.ok("User logged out successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<AuthInfoDto> getAuthenticatedUser() {
        return ResponseEntity.ok(userService.getAuthenticatedUserInfo(authService.getAuthenticatedUsername()));
    }
}
