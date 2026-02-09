package gr.aegean.icsd.autorepair.user;


import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import gr.aegean.icsd.autorepair.shared.exception.EntitySavingException;
import gr.aegean.icsd.autorepair.shared.exception.InvalidOperationException;
import gr.aegean.icsd.autorepair.shared.exception.NoChangesDetectedException;
import gr.aegean.icsd.autorepair.shared.exception.ParsingFileIOException;
import gr.aegean.icsd.autorepair.shared.security.AuthInfoDto;
import gr.aegean.icsd.autorepair.shared.security.service.AuthService;
import gr.aegean.icsd.autorepair.shared.security.service.JwtService;
import gr.aegean.icsd.autorepair.shared.utils.AuthUtils;
import gr.aegean.icsd.autorepair.shared.utils.CsvParser;
import gr.aegean.icsd.autorepair.shared.validators.GenericValidator;
import gr.aegean.icsd.autorepair.shared.validators.ValidCsvFileType;
import gr.aegean.icsd.autorepair.shared.validators.ValidExcelFileType;
import gr.aegean.icsd.autorepair.user.customer.Customer;
import gr.aegean.icsd.autorepair.user.customer.CustomerRegistrationDto;
import gr.aegean.icsd.autorepair.user.customer.CustomerService;
import gr.aegean.icsd.autorepair.user.customer.CustomerUpdateDto;
import gr.aegean.icsd.autorepair.user.mechanic.Mechanic;
import gr.aegean.icsd.autorepair.user.mechanic.MechanicRegistrationDto;
import gr.aegean.icsd.autorepair.user.mechanic.MechanicService;
import gr.aegean.icsd.autorepair.user.mechanic.MechanicUpdateDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthUtils authUtils;
    private final UserMapper userMapper;
    private final CsvParser csvParser;
    private final CustomerService customerService;
    private final MechanicService mechanicService;
    private final GenericValidator genericValidator;
    private final AuthService authService;

    @Transactional
    public void changePassword(@Valid ChangePasswordDto changePasswordDto, HttpServletResponse response) {
        User user = getAuthenticatedUser();
        String storedHash = user.getPassword();

        if (!passwordEncoder.matches(changePasswordDto.oldPassword(), storedHash)) {
            throw new InvalidOperationException("Old password does not match");
        }

        if (passwordEncoder.matches(changePasswordDto.newPassword(), storedHash)) {
            throw new InvalidOperationException("New password cannot be the same as the old password");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDto.newPassword()));
        userRepository.save(user);

        authService.revokeToken(response);
    }

    @Transactional
    public UserDetailsDto getUserEntityDetails() {
        return userMapper.mapEntityToDto(getAuthenticatedUser());
    }

    private User getAuthenticatedUser() {
        return getUserByUsername(authUtils.getAuthenticatedUsername());
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public void modifyLockedAccount(Long id, boolean active) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        user.setActive(active);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {

        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if (user.getRole().equals(UserRole.SECRETARY)) {
            throw new InvalidOperationException("You can't delete secretary account");
        }
        userRepository.delete(user);
    }

    public void deleteUsers(Set<Long> ids){
        ids.forEach(this::deleteUser);
    }

    @Transactional
    public void deleteMyAccount(@NonNull HttpServletResponse response) {
        if(getAuthenticatedUser().getRole().equals(UserRole.SECRETARY)){
            throw new InvalidOperationException("You can't delete secretary account");
        }

        userRepository.delete(getAuthenticatedUser());
        jwtService.clearJwtCookie(response);
    }

    @Transactional
    public UserDetailsDto loadUserById(@NonNull Long id) {
        return userMapper.mapEntityToDto(getUserByID(id));
    }

    @Transactional
    public Set<UserDetailsDto> loadAllOtherUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !user.getUsername().equals(getAuthenticatedUser().getUsername()))
                .map(userMapper::mapEntityToDto).collect(Collectors.toSet());
    }
    @Transactional
    public Page<UserDetailsDto> searchPageUsers(
            String username,
            String lastName,
            String taxNumber,
            String keyword,
            Pageable pageable,
            UserRole role
    ) {
        Specification<User> spec = UserSpecifications.withFilters(username, lastName, taxNumber, keyword,role);

        return userRepository.findAll(spec, pageable)
                .map(userMapper::mapEntityToDto);
    }
    @Transactional
    public Page<UserDetailsDto> searchPageInactiveUsersByKeyword(
            String keyword,
            Pageable pageable
    ) {
        Specification<User> spec = UserSpecifications.withKeywordSearchInactiveUsers(keyword);
        return userRepository.findAll(spec,pageable)
                .map(userMapper::mapEntityToDto);
    }



    @Transactional
    public String addUsersByCsv(@ValidCsvFileType MultipartFile file) {
        try {

            List<UserFileRepresentation> usersFromFile = csvParser.parseCsv(file, UserFileRepresentation.class);

            return processUsersFileRepresentations(usersFromFile);

        } catch (IOException e) {
            log.error("Error processing file ", e);
            throw new ParsingFileIOException();
        }


    }

    @Transactional
    public String addUsersByExcel(@ValidExcelFileType MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {

            List<UserFileRepresentation> usersFromFile = Poiji.fromExcel(inputStream, PoijiExcelType.XLSX, UserFileRepresentation.class);

            return processUsersFileRepresentations(usersFromFile);

        } catch (IOException e) {
            log.error("Error processing file", e);
            throw new ParsingFileIOException();
        }

    }

    private String processUsersFileRepresentations(List<UserFileRepresentation> usersFromFile) {
        Set<UserRegistrationDto> userDtos = userMapper.mapUserFileRepresentationToNewUserDto(usersFromFile);
        Set<UserRegistrationDto> validUserDtos = genericValidator.validateAndFilter(userDtos);
        int sumOfSavedUsers = saveMultipleUsers(validUserDtos);

        if (usersFromFile.size() == validUserDtos.size()) {
            return "Successfully processed and saved " + sumOfSavedUsers + " users.";
        }
        if (sumOfSavedUsers == 0) {
            throw new UserAlreadyExistsException("Couldn't save users because they already exist. ");
        }

        return "Failed to save all given users. Number of users saved is: " + sumOfSavedUsers;
    }


    public int saveMultipleUsers(Set<UserRegistrationDto> users) {
        AtomicInteger sum = new AtomicInteger();
        users.forEach(userDto -> {
            try {
                saveUser(userDto);
                sum.getAndIncrement();
            } catch (DataIntegrityViolationException ex) {
                log.warn("User {} already exist!", userDto.toString());
            }
        });
        return sum.get();
    }

    public void saveUser(UserRegistrationDto dto) {
        UserRole role = dto.getRole(); // already an enum

        switch (role) {
            case CUSTOMER -> customerService.addCustomer((CustomerRegistrationDto) dto);
            case MECHANIC -> mechanicService.addMechanic((MechanicRegistrationDto) dto);
            default -> throw new IllegalArgumentException("Unsupported role: " + role);
        }
    }

    public AuthInfoDto getAuthenticatedUserInfo(String username) {
        return userRepository.findAuthInfoByUsername(username);
    }


    private UserDetailsDto updateUser(User user, UserUpdateDto dto){
        boolean updated = false;
        updated |= updateUsernameIfNeeded(user, dto.getUsername());
        updated |= updateEmailIfNeeded(user, dto.getEmail());
        updated |= updateFirstNameIfNeeded(user, dto.getFirstName());
        updated |= updateLastNameIfNeeded(user, dto.getLastName());
        updated |= updateIdentityNumberIfNeeded(user, dto.getIdentityNumber());

        if (user.getRole().equals(UserRole.CUSTOMER)) {
            updated |= customerService.updateCustomerFields((Customer) user, (CustomerUpdateDto) dto);
        }
        if (user.getRole().equals(UserRole.MECHANIC)) {
            updated |= mechanicService.updateMechanicFields((Mechanic) user, (MechanicUpdateDto) dto);
        }

        if (!updated) {
            throw new NoChangesDetectedException();
        }
        try {
            user = userRepository.save(user);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new EntitySavingException("Unexpected exception during saving user");
        }
        return userMapper.mapEntityToDto(user);

    }
    @Transactional
    public UserDetailsDto updateOwnUserDetails(UserUpdateDto dto) {
        User user = getAuthenticatedUser();
        return updateUser(user, dto);
    }

    @Transactional
    public UserDetailsDto updateUserById(Long id, @Valid UserUpdateDto dto) {
        User user = getUserByID(id);
        return updateUser(user,dto);
    }

    private boolean updateUsernameIfNeeded( User user, String newUsername) {
        if(null==newUsername || newUsername.isBlank()) return false;
        if (user.getUsername().equals(newUsername)) return false;
        user.setUsername(newUsername);
        return true;
    }

    private boolean updateEmailIfNeeded( User user,  String email) {
        if(null==email || email.isBlank()) return false;
        if (user.getEmail().equals(email)) return false;
        user.setEmail(email);
        return true;
    }

    private boolean updateFirstNameIfNeeded( User user, String firstName) {
        if(null==firstName|| firstName.isBlank()) return false;
        if (user.getFirstName().equals(firstName)) return false;
        user.setFirstName(firstName);
        return true;
    }

    private boolean updateLastNameIfNeeded( User user,  String lastName) {
        if(null==lastName || lastName.isBlank()) return false;
        if (user.getLastName().equals(lastName)) return false;
        user.setLastName(lastName);
        return true;
    }

    private boolean updateIdentityNumberIfNeeded(User user, String identityNumber) {
        if(null==identityNumber || identityNumber.isBlank()) return false;
        if (user.getIdentityNumber().equals(identityNumber)) return false;
        user.setIdentityNumber(identityNumber);
        return true;
    }

    private User getUserByID(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }
}
