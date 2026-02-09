package gr.aegean.icsd.autorepair.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum UserRole implements GrantedAuthority {
    SECRETARY,
    MECHANIC,
    CUSTOMER,
    ANONYMOUS;
    @JsonCreator
    public static UserRole from(String value) {
        return value == null ? null : UserRole.valueOf(value.toUpperCase());
    }
    @Override
    public String getAuthority() {
        return this.name();
    }
}