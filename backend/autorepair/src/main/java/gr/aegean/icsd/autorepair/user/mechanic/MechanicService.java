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

    private boolean updateSpecialtyIfNeeded(@NonNull Mechanic user,@NonNull String specialty) {
        if(null!= user.getSpecialty() && user.getSpecialty().equals(specialty)) {
            return false;
        }
        user.setSpecialty(specialty);
        return true;
    }
//todo να τα φτιάξω δεν τα έχω υλοποιημένα
    public UserDetailsDto loadMechanicById(@NonNull Long id) {
        return mapper.mapDtoToEntity()repository.findById(id);

    }
}
