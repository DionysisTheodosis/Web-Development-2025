package gr.aegean.icsd.autorepair.dashboard;

public record CustomerDashboardDTO(
        String dashboardType, // Will be "CUSTOMER"
        long myVehiclesCount,
        String nextAppointmentDate
) {}
