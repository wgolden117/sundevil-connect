package ser460.sundevilconnect.client.events;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import ser460.sundevilconnect.client.ConnectionManager;
import ser460.sundevilconnect.client.CurrentUser;
import ser460.sundevilconnect.shared.proto.EventRegistrationServiceProto;

public class MyEventsView {

    @FXML
    private VBox eventsContainer;

    @FXML
    private void initialize() {
        loadMyEvents();
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

            eventsContainer.getChildren().clear();

            if (registrations.isEmpty()) {
                eventsContainer.getChildren().add(new Label("No registered events."));
                return;
            }

            registrations.forEach(reg -> {
                var event = reg.getEvent();

                Label label = new Label(
                        event.getTitle() +
                                "\n" + event.getLocation() +
                                " | " + event.getCategory()
                );

                label.setStyle("-fx-padding: 10; -fx-border-color: lightgray;");

                eventsContainer.getChildren().add(label);
            });

        } catch (Exception e) {
            System.err.println("Error loading my events: " + e.getMessage());
        }
    }
}