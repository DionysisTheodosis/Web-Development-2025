package gr.aegean.icsd.autorepair.user;


import gr.aegean.icsd.autorepair.user.customer.Customer;
import gr.aegean.icsd.autorepair.user.customer.CustomerRegistrationDto;
import gr.aegean.icsd.autorepair.user.mechanic.Mechanic;
import gr.aegean.icsd.autorepair.user.mechanic.MechanicRegistrationDto;

import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDetailsDto mapBaseUserToDto(@NonNull User user) {
        return UserDetailsDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .identityNumber(user.getIdentityNumber())
                .role(user.getRole().name())
                .active(user.isActive())
                .build();
    }

    UserDetailsDto mapEntityToDto(@NonNull User user) {
        UserDetailsDto.UserDetailsDtoBuilder dtoBuilder = UserDetailsDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .active(user.isActive());
        if (user instanceof Customer customer) {
            dtoBuilder.address(customer.getAddress())
                    .taxNumber(customer.getTaxNumber());
        } else if (user instanceof Mechanic mechanic) {
            dtoBuilder.specialty(mechanic.getSpecialty());
        }
        return dtoBuilder.build();
    }

    public UserRegistrationDto mapUserFileRepresentationToUserRegistrationDto(@NonNull UserFileRepresentation userFileRepresentation) {

        if ("mechanic".equalsIgnoreCase(userFileRepresentation.getRole())) {
            return MechanicRegistrationDto.builder()
                    .username(userFileRepresentation.getUsername())
                    .password(userFileRepresentation.getPassword())
                    .email(userFileRepresentation.getEmail())
                    .firstName(userFileRepresentation.getFirstName())
                    .lastName(userFileRepresentation.getLastName())
                    .identityNumber(userFileRepresentation.getIdentityNumber())
                    .specialty(userFileRepresentation.getSpecialty())
                    .role(userFileRepresentation.getRole())
                    .build();
        } else if ("customer".equalsIgnoreCase(userFileRepresentation.getRole())) {
            return  CustomerRegistrationDto.builder()
                    .username(userFileRepresentation.getUsername())
                    .password(userFileRepresentation.getPassword())
                    .email(userFileRepresentation.getEmail())
                    .firstName(userFileRepresentation.getFirstName())
                    .lastName(userFileRepresentation.getLastName())
                    .identityNumber(userFileRepresentation.getIdentityNumber())
                    .role(userFileRepresentation.getRole())
                    .taxNumber(userFileRepresentation.getTaxNumber())
                    .address(userFileRepresentation.getAddress())
                    .build();
        }
        return null;
    }

    public Set<UserRegistrationDto> mapUserFileRepresentationToNewUserDto(@NonNull List<UserFileRepresentation> users) {
        return users.stream()
                .map(this:: mapUserFileRepresentationToUserRegistrationDto)
                .collect(Collectors.toSet());
    }


}