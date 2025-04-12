package gr.aegean.icsd.autorepair.shared.security.controller;


import gr.aegean.icsd.autorepair.shared.security.LoginRequestDto;
import gr.aegean.icsd.autorepair.shared.security.service.AuthService;
import gr.aegean.icsd.autorepair.user.UserRegistrationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequestDto dto, HttpServletResponse response) {
        return ResponseEntity.ok(authService.authenticateAndSetToken(dto, response));
    }

    @Operation(
            summary = "Register a new user",
            description = "Registration payload which can be a Mechanic or a Customer",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserRegistrationDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "MechanicExample",
                                            summary = "A mechanic registration example",
                                            value = """
                                    {
                                      "username": "Dionysis",
                                      "password": "Dion123@!",
                                      "email": "theo_32@htm.com",
                                      "firstName": "Dionysis",
                                      "lastName": "Dionysis",
                                      "identityNumber": "AT123456",
                                      "role": "mechanic",
                                      "specialty": "computer engineer"
                                    }
                                    """
                                    ),
                                    @ExampleObject(
                                            name = "CustomerExample",
                                            summary = "A customer registration example",
                                            value = """
                                    {
                                      "username": "JaneDoe",
                                      "password": "Jane123@!",
                                      "email": "jane_doe@example.com",
                                      "firstName": "Jane",
                                      "lastName": "Doe",
                                      "identityNumber": "AT654321",
                                      "role": "customer",
                                      "taxNumber": "TAX789",
                                      "address": "123 Main St"
                                    }
                                    """
                                    )
                            }
                    )
            )
    )
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid UserRegistrationDto dto) {
        authService.registerUser(dto);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        authService.revokeToken(response);
        return ResponseEntity.ok("User logged out successfully");
    }

/*


    @PostMapping("/register/mechanics")
    public ResponseEntity<String> registerMechanics(){}

*/
/*    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequestDto registerRequest) {
        registerService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }*//*


    @GetMapping("/isSignIn")
    public ResponseEntity<Boolean> isSignIn() {
        return ResponseEntity.ok().body(loginService.isSignIn());
    }

    @GetMapping("/user-role")
    public ResponseEntity<String> getUserRole() {
        return ResponseEntity.ok().body(loginService.getUserRole());
    }
*/
//@GetMapping("/valid/secretary")
//public ResponseEntity<Boolean> validSecretary() {
//    return ResponseEntity.accepted().body(accessService.validateLoggedInSecretary());
//
//}
}
