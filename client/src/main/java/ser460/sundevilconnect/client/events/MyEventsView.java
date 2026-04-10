package ser460.sundevilconnect.client.events;

import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import ser460.sundevilconnect.client.ConnectionManager;
import ser460.sundevilconnect.client.CurrentUser;
import ser460.sundevilconnect.shared.proto.EventRegistrationServiceProto;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class MyEventsView implements FilterListener{

    @FXML private ListView<EventRegistrationServiceProto.EventRegistration> eventListView;
    @FXML private AnchorPane detailsPane;
    @FXML private EventFilterPanel filterPanelController;
    private java.util.List<EventRegistrationServiceProto.EventRegistration> allRegistrations;
    @SuppressWarnings("unused")
    @FXML private AnchorPane filterPanel;

    @FXML
    private void initialize() {
        setupListView();
        setupClickHandler();
        loadMyEvents();

        if (filterPanelController != null) {
            filterPanelController.setFilterListener(this);
        }
    }

    private void setupListView() {
        eventListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(EventRegistrationServiceProto.EventRegistration reg, boolean empty) {
                super.updateItem(reg, empty);

                if (empty || reg == null) {
                    setText(null);
                } else {
                    var event = reg.getEvent();
                    setText(event.getTitle()
                            + "\n" + event.getLocation()
                            + " | " + event.getCategory());
                }
            }
        });
    }

    private void setupClickHandler() {
        eventListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        loadEventDetails(newVal);
                    }
                }
        );
    }

    private void loadEventDetails(EventRegistrationServiceProto.EventRegistration reg) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/events/event_details.fxml")
            );

            Parent view = loader.load();

            EventDetailsView controller = loader.getController();

            controller.setMyEvent(
                    reg.getEvent(),
                    reg.getRegistrationId(),
                    this::loadMyEvents // refresh after cancel
            );

            detailsPane.getChildren().setAll(view);

        } catch (Exception e) {
            System.err.println("Error loading details: " + e.getMessage());
        }
    }

    private void loadMyEvents() {
        try {
            var stub = ConnectionManager.getInstance().getEventRegistrationStub();

            String studentId = CurrentUser.getInstance().getUserId();

            var request = EventRegistrationServiceProto.GetRegistrationsForStudentRequest
                    .newBuilder()
                    .setStudentId(studentId)
                    .build();

            var response = stub.getRegistrationsForStudent(request);

            var registrations = response.getRegistrationsList();

            allRegistrations = registrations; // store original list

            eventListView.getItems().setAll(registrations);
            if (registrations.isEmpty()) {
                eventListView.setPlaceholder(
                        new javafx.scene.control.Label("You haven't registered for any events yet")
                );
            } else {
                eventListView.setPlaceholder(null);
            }
            detailsPane.getChildren().clear();

        } catch (Exception e) {
            System.err.println("Error loading my events: " + e.getMessage());
        }
    }

    @Override
    public void onFiltersApplied(
            String category,
            boolean paid,
            java.time.LocalDate fromDate,
            java.time.LocalDate toDate,
            String club
    ) {
        var filtered = allRegistrations.stream()
                .filter(reg -> {
                    var event = reg.getEvent();

                    // CATEGORY
                    if (category != null && !category.isEmpty()) {
                        if (!event.getCategory().equalsIgnoreCase(category)) {
                            return false;
                        }
                    }

                    // PAID
                    if (paid) {
                        if (!event.getIsPaid()) {
                            return false;
                        }
                    }

                    // DATE
                    if (fromDate != null && toDate != null) {
                        var eventDate = java.time.LocalDate.parse(event.getEventDate());

                        if (eventDate.isBefore(fromDate) || eventDate.isAfter(toDate)) {
                            return false;
                        }
                    }

                    // CLUB
                    if (club != null && !club.isEmpty()) {
                        return event.hasHostedBy() &&
                                event.getHostedBy().getClubId().equals(club);
                    }

                    return true;
                })
                .toList();

        eventListView.getItems().setAll(filtered);
        if (filtered.isEmpty()) {
            eventListView.setPlaceholder(new javafx.scene.control.Label("No matching events found"));
        } else {
            eventListView.setPlaceholder(null);
        }
        detailsPane.getChildren().clear();
    }

    @Override
    public void onFiltersCleared() {
        System.out.println("Filters cleared → showing all my events");

        eventListView.getItems().setAll(allRegistrations);

        if (allRegistrations == null || allRegistrations.isEmpty()) {
            eventListView.setPlaceholder(
                    new javafx.scene.control.Label("You haven't registered for any events yet")
            );
        } else {
            eventListView.setPlaceholder(null);
        }

        detailsPane.getChildren().clear();
    }
}