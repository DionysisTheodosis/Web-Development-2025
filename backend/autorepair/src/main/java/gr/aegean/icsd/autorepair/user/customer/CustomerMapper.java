package gr.aegean.icsd.autorepair.user.customer;


import gr.aegean.icsd.autorepair.user.UserRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component

public class CustomerMapper {
    private final PasswordEncoder passwordEncoder;

    public Customer mapDtoToEntity(CustomerRegistrationDto dto) {
            return Customer.builder()
                    .username(dto.getUsername())
                    .firstName(dto.getFirstName())
                    .lastName(dto.getLastName())
                    .email(dto.getPassword())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .identityNumber(dto.getIdentityNumber())
                    .role(UserRole.MECHANIC)
                    .address(dto.getAddress())
                    .taxNumber(dto.getTaxNumber())
                    .build();
    }
}
