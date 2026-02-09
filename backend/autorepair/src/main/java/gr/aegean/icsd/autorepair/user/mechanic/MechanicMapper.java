package gr.aegean.icsd.autorepair.user.mechanic;

import gr.aegean.icsd.autorepair.user.UserDetailsDto;
import gr.aegean.icsd.autorepair.user.UserRole;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MechanicMapper{
    private final PasswordEncoder passwordEncoder;
    public Mechanic  mapDtoToEntity(@NonNull  MechanicRegistrationDto dto) {
        return Mechanic.builder()
                .username(dto.getUsername())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .identityNumber(dto.getIdentityNumber())
                .role(UserRole.MECHANIC)
                .specialty(dto.getSpecialty())
                .build();
    }

    public UserDetailsDto mapEntityToDto(@NonNull Mechanic entity,UserRole role) {
        UserDetailsDto.UserDetailsDtoBuilder builder = UserDetailsDto.builder();
        builder
                .identityNumber(entity.getIdentityNumber())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .specialty(entity.getSpecialty());
        if(role == UserRole.MECHANIC){

        }
        else if (role == UserRole.SECRETARY) {

        }
        else if(role == UserRole.CUSTOMER){
            builder
                    .identityNumber(entity.getIdentityNumber())
                    .firstName(entity.getFirstName())
                    .lastName(entity.getLastName())
                    .specialty(entity.getSpecialty());
        }
        return builder.build();
    }
}
