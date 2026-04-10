package ser460.sundevilconnect.client.events;

public interface FilterListener {
    void onFiltersApplied(
            String category,
            boolean paid,
            java.time.LocalDate fromDate,
            java.time.LocalDate toDate,
            String club
    );

    void onFiltersCleared();
}