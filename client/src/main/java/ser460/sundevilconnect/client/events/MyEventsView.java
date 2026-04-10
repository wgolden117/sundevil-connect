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

public class MyEventsView {

    @FXML private ListView<EventRegistrationServiceProto.EventRegistration> eventListView;
    @FXML private AnchorPane detailsPane;

    @FXML
    private void initialize() {
        setupListView();
        setupClickHandler();
        loadMyEvents();
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

            eventListView.getItems().setAll(registrations);
            detailsPane.getChildren().clear();

        } catch (Exception e) {
            System.err.println("Error loading my events: " + e.getMessage());
        }
    }
}