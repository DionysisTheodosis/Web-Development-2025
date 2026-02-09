package gr.aegean.icsd.autorepair.dashboard;

public record MechanicDashboardDTO(
        String dashboardType, // Will be "MECHANIC"
        long myActiveRepairs, // Placeholder since we don't have Repair entity yet
        long pendingAppointments
) {}