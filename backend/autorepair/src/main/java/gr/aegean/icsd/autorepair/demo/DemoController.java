package gr.aegean.icsd.autorepair.demo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/session-cookie")
@Tag(name = "Demo")
@Profile("dev")
public class DemoController {

    @Operation(
            description = "Demo Controller to get the JWT cookie value (dev only)"
    )
    @GetMapping("")
    public ResponseEntity<String> returnJwtCookie(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        String jwtCookieValue = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    jwtCookieValue = cookie.getValue();
                    break;
                }
            }
        }

        if (jwtCookieValue == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("JWT Cookie not found");
        }

        return ResponseEntity.status(HttpStatus.OK).body("JWT Cookie value: " + jwtCookieValue);
    }
}