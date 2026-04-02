package ser460.sundevilconnect.client.events;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import ser460.sundevilconnect.shared.proto.EntitiesProto.Event;
import ser460.sundevilconnect.client.ConnectionManager;
import ser460.sundevilconnect.shared.proto.EventRegistrationServiceProto;

public class EventDetailsView {

    @FXML private Label titleLabel;
    @FXML private Label categoryLabel;
    @FXML private Button registerButton;

    private Event currentEvent;

    public void setEvent(Event event) {
        this.currentEvent = event;
        populateUI();
    }

    private void populateUI() {
        if (currentEvent != null) {
            titleLabel.setText(currentEvent.getTitle());
            categoryLabel.setText(currentEvent.getCategory());
        }
    }

    @FXML
    private void handleRegister() {
        if (currentEvent == null) return;

        try {
            var stub = ConnectionManager.getInstance().getEventRegistrationStub();

            // TEMP: replace later with CurrentUser.getUserId()
            String studentId = "student1";

            var request = EventRegistrationServiceProto.RegisterStudentForEventRequest
                    .newBuilder()
                    .setStudentId(studentId)
                    .setEventId(currentEvent.getEventId())
                    .build();

            registerButton.setDisable(true);

            var response = stub.registerStudentForEvent(request);

            if (response != null) {
                registerButton.setText("Registered");
            }

            System.out.println("Registered for event: " + currentEvent.getTitle());

        } catch (Exception e) {
            registerButton.setDisable(false);
            System.err.println("Registration failed: " + e.getMessage());
        }
    }

    @FXML
    private void initialize() {
        // nothing needed yet
    }
}