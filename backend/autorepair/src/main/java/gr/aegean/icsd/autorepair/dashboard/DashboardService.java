// src/main/java/com/autorepair/service/DashboardService.java

package gr.aegean.icsd.autorepair.dashboard;

import gr.aegean.icsd.autorepair.shared.utils.AuthUtils;
import gr.aegean.icsd.autorepair.user.UserRepository;
import gr.aegean.icsd.autorepair.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final AuthUtils authUtils; // ✅ Inject the helper

    @Transactional(readOnly = true)
    public Object getStats() { // ❌ No parameter needed anymore

        // 1. Get User Details via Helper
        String currentUsername = authUtils.getAuthenticatedUsername();
        String role = authUtils.getAuthenticatedUserRole(); // Returns "SECRETARY", "MECHANIC", etc.

        // Debugging: Print this to your console to see EXACTLY what it returns
        System.out.println("DEBUG DASHBOARD: User [" + currentUsername + "] has Role [" + role + "]");

        // 2. LOGIC SWITCH
        // We use .contains() or .equalsIgnoreCase() to be safe
        if (role.contains("SECRETARY")) {
            return getSecretaryData();
        }
        else if (role.contains("MECHANIC")) {
            return getMechanicData(currentUsername);
        }
        else {
            return getCustomerData(currentUsername);
        }
    }

    // --- Private Methods (Keep existing logic) ---

    // src/main/java/gr/aegean/icsd/autorepair/dashboard/DashboardService.java

    private SecretaryDashboardDTO getSecretaryData() {
        // 1. Basic Counts
        long total = userRepository.count();
        long pending = userRepository.countByActiveFalse();

        // 2. Role Specific Counts (Active Users Only)
        // Ensure you have these methods in UserRepository or use a custom query
        long mechanics = userRepository.countByRoleAndActiveTrue(UserRole.MECHANIC);
        long customers = userRepository.countByRoleAndActiveTrue(UserRole.CUSTOMER);
        long secretaries = userRepository.countByRoleAndActiveTrue(UserRole.SECRETARY);

        // 3. Return the Flat DTO
        return new SecretaryDashboardDTO(
                "SECRETARY",
                total,
                pending,
                mechanics,
                customers,
                secretaries
        );
    }
    private MechanicDashboardDTO getMechanicData(String username) {
        return new MechanicDashboardDTO("MECHANIC", 5, 2);
    }

    private CustomerDashboardDTO getCustomerData(String username) {
        return new CustomerDashboardDTO("CUSTOMER", 1, "2024-05-20");
    }
}