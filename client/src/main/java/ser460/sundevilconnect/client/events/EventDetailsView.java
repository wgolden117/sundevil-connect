package ser460.sundevilconnect.client.events;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import ser460.sundevilconnect.client.CurrentUser;
import ser460.sundevilconnect.shared.proto.EntitiesProto.Event;
import ser460.sundevilconnect.client.ConnectionManager;
import ser460.sundevilconnect.shared.proto.EventRegistrationServiceProto;

public class EventDetailsView {

    @FXML private Label titleLabel;
    @FXML private Label categoryLabel;
    @FXML private Button registerButton;
    @FXML private Label locationLabel;
    @FXML private Label dateLabel;

    private Event currentEvent;

    public void setEvent(Event event) {
        this.currentEvent = event;
        populateUI();
    }

    private void populateUI() {
        if (currentEvent != null) {
            titleLabel.setText(currentEvent.getTitle());
            categoryLabel.setText(currentEvent.getCategory());
            locationLabel.setText(currentEvent.getLocation());
            dateLabel.setText(currentEvent.getEventDate());
        }
    }

    private void showAlert(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION
        );
        alert.setTitle("Event Registration");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleRegister() {
        if (currentEvent == null) return;

        try {
            var stub = ConnectionManager.getInstance().getEventRegistrationStub();

            String studentId = CurrentUser.getInstance().getUserId();

            var request = EventRegistrationServiceProto.RegisterStudentForEventRequest
                    .newBuilder()
                    .setStudentId(studentId)
                    .setEventId(currentEvent.getEventId())
                    .build();

            var response = stub.registerStudentForEvent(request);

            if (response.getSuccess()) {
                registerButton.setText("Registered");
            } else {
                registerButton.setDisable(false);
                showAlert(response.getMessage());
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