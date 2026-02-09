package gr.aegean.icsd.autorepair.dashboard;


public record SecretaryDashboardDTO(
        String dashboardType,
        long totalUsers,
        long pendingUsers,
        long activeMechanics, // ✅ Explicit Field
        long activeCustomers, // ✅ Explicit Field
        long activeSecretaries // ✅ Explicit Field (for the Chart)
) {}
