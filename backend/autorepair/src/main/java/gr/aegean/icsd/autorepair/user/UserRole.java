package gr.aegean.icsd.autorepair.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collections;
import java.util.Set;

@RequiredArgsConstructor
public enum UserRole implements GrantedAuthority {
    SECRETARY,
    MECHANIC,
    CUSTOMER,
    ANONYMOUS;

    @Override
    public String getAuthority() {
        return this.name();
    }
}