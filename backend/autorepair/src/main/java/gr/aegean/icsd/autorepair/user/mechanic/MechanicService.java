package gr.aegean.icsd.autorepair.user.mechanic;

import gr.aegean.icsd.autorepair.user.UserDetailsDto;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MechanicService {
    private final  MechanicMapper mapper;
    private final MechanicRepository repository;

    @Transactional
    public void addMechanic(MechanicRegistrationDto dto) {
        repository.save(mapper.mapDtoToEntity(dto));
    }

    public boolean updateMechanicFields(@NonNull Mechanic user,@NonNull MechanicUpdateDto dto) {
        return updateSpecialtyIfNeeded(user,dto.getSpecialty());
    }

    private boolean updateSpecialtyIfNeeded(Mechanic user, String newSpecialty) {
        if (newSpecialty == null || newSpecialty.isBlank()) {
            return false;
        }
        if (newSpecialty.equals(user.getSpecialty())) {
            return false;
        }
        user.setSpecialty(newSpecialty);
        return true;
    }

}
