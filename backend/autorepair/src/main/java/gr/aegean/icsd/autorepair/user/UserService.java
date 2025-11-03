package gr.aegean.icsd.autorepair.user;


import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import gr.aegean.icsd.autorepair.shared.exception.EntitySavingException;
import gr.aegean.icsd.autorepair.shared.exception.InvalidOperationException;
import gr.aegean.icsd.autorepair.shared.exception.NoChangesDetectedException;
import gr.aegean.icsd.autorepair.shared.exception.ParsingFileIOException;
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


    @Transactional
    public void changePassword(@Valid ChangePasswordDto changePasswordDto) {
        User user = getAuthenticatedUser();
        if (!user.getPassword().equals(passwordEncoder.encode(changePasswordDto.oldPassword()))) {
            throw new InvalidOperationException("Old password does not match");
        }
        if (user.getPassword().equals(passwordEncoder.encode(changePasswordDto.newPassword()))) {
            throw new InvalidOperationException("New password cannot be the same as the old password");
        }
        user.setPassword(passwordEncoder.encode(changePasswordDto.newPassword()));
        userRepository.save(user);
    }

    //todo to change how the details of user profile returns to each user for own profile
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
        userRepository.delete(user);
    }

    @Transactional
    public void deleteMyAccount(@NonNull HttpServletResponse response) {
        userRepository.delete(getAuthenticatedUser());
        jwtService.clearJwtCookie(response);
    }

    /*    @Transactional
        public UserDetailsDto loadUserByUsername(@NonNull String username) {
            User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
            return userMapper.mapEntityToDto(user);
        }

        @Transactional
        public UserDetailsDto loadUserById(@NonNull Long id) {
            User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
            return userMapper.mapEntityToDto(user);
        }*/
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
            PageRequest pageRequest
    ) {
        Specification<User> spec = UserSpecifications.withFilters(
                username, lastName, taxNumber);

        return userRepository.findAll(spec, pageRequest)
                .map(userMapper::mapEntityToDto);
    }

    @Transactional
    public Page<UserDetailsDto> searchPageInactiveUsersByKeyword(
            String keyword,
            PageRequest pageRequest
    ) {
        Specification<User> spec = UserSpecifications.withKeywordSearchInactiveUsers(keyword);
        return userRepository.findAll(spec, pageRequest)
                .map(userMapper::mapEntityToDto);
    }


    @Transactional
    public Page<UserDetailsDto> searchPageUsersByKeyword(
            String keyword,
            PageRequest pageRequest
    ) {
        Specification<User> spec = UserSpecifications.withKeywordSearch(keyword);
        return userRepository.findAll(spec, pageRequest)
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

/*
    public void saveUser(UserRegistrationDto userDto) {
        userRepository.save(userMapper.mapDtoToEntity(userDto));
    }
*/

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
        if (dto.getRole().equalsIgnoreCase(UserRole.CUSTOMER.name())) {
            customerService.addCustomer((CustomerRegistrationDto) dto);
        } else if (dto.getRole().equalsIgnoreCase(UserRole.MECHANIC.name())) {
            mechanicService.addMechanic((MechanicRegistrationDto) dto);
        }
    }

    @Transactional
    public UserDetailsDto updateOwnUserDetails(UserUpdateDto dto) {
        User user = getAuthenticatedUser();
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

    private boolean updateUsernameIfNeeded(@NonNull User user, @NonNull String newUsername) {
        if (null != user.getUsername() && user.getUsername().equals(newUsername)) {
            return false;
        }
        user.setUsername(newUsername);
        return true;
    }

    private boolean updateEmailIfNeeded(@NonNull User user, @NonNull String email) {
        if (null != user.getEmail() && user.getEmail().equals(email)) {
            return false;
        }
        user.setEmail(email);
        return true;
    }

    private boolean updateFirstNameIfNeeded(@NonNull User user, @NonNull String firstName) {
        if (null != user.getFirstName() && user.getFirstName().equals(firstName)) {
            return false;
        }
        user.setUsername(firstName);
        return true;
    }

    private boolean updateLastNameIfNeeded(@NonNull User user, @NonNull String lastName) {
        if (null != user.getLastName() && user.getLastName().equals(lastName)) {
            return false;
        }
        user.setLastName(lastName);
        return true;
    }

    private boolean updateIdentityNumberIfNeeded(@NonNull User user, @NonNull String identityNumber) {
        if (null != user.getIdentityNumber() && user.getIdentityNumber().equals(identityNumber)) {
            return false;
        }
        user.setIdentityNumber(identityNumber);
        return true;
    }

    private User getUserByID(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }
/*

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtils authUtil;
    private final UserMapper userMapper;

    public Optional<User> saveUser(User user) {
        return Optional.of(userRepository.save(user));
    }

    public Boolean isUserExistsByPersonalIDAndEmail(String personalID, String email) {
        return userRepository.existsByIdentityNumberOrEmail(personalID, email);
    }

    public boolean isUserExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User getAuthenticatedUser() {
        String email = authUtil.getAuthenticatedUserEmail();
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }



    public UserDetailsDto getOwnUserDetails() {
        User user = getAuthenticatedUser();
        return userMapper.userEntityToUserDetailsDto(user);
    }



    public Set<User> getUsers(UserRole userRole) {
        return userRepository.findAllByRole(userRole).orElseThrow(UserNotFoundException::new);
    }*/
}
