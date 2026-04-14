package ser460.sundevilconnect.client.events;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import ser460.sundevilconnect.client.CurrentUser;
import ser460.sundevilconnect.shared.proto.EntitiesProto.Event;
import ser460.sundevilconnect.client.ConnectionManager;
import ser460.sundevilconnect.shared.proto.EventRegistrationServiceProto;

public class EventDetailsView {

    @FXML private Label titleLabel;
    @FXML private Label categoryLabel;
    @FXML private TextArea descriptionLabel;
    @FXML private Button registerButton;
    @FXML private Label locationLabel;
    @FXML private Label dateLabel;
    @FXML private Label capacityLabel;

    private Event currentEvent;
    private boolean isMyEvent = false;
    private String registrationId;
    private Runnable onCancelSuccess; // callback to refresh My Events

    public void setEvent(Event event) {
        this.currentEvent = event;
        populateUI();
    }

    public void setMyEvent(Event event, String registrationId, Runnable onCancelSuccess) {
        this.currentEvent = event;
        this.registrationId = registrationId;
        this.isMyEvent = true;
        this.onCancelSuccess = onCancelSuccess;

        populateUI();
        registerButton.setText("Cancel Registration");
    }

    private void populateUI() {
        if (currentEvent != null) {
            titleLabel.setText(currentEvent.getTitle());
            categoryLabel.setText(currentEvent.getCategory());
            locationLabel.setText(currentEvent.getLocation());
            dateLabel.setText(currentEvent.getEventDate());
            descriptionLabel.setText(currentEvent.getDescription());
            capacityLabel.setText(String.valueOf(currentEvent.getCapacity()));
        }
    }

    public void hideRegisterButton() {
        registerButton.setVisible(false);
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

            // Cancel flow
            if (isMyEvent) {
                var cancelRequest = EventRegistrationServiceProto.CancelRegistrationRequest
                        .newBuilder()
                        .setRegistrationId(registrationId)
                        .build();

                var cancelResponse = stub.cancelRegistration(cancelRequest);

                if (cancelResponse.getSuccess()) {
                    showAlert("Registration cancelled.");
                    registerButton.setDisable(true);

                    // refresh My Events view
                    if (onCancelSuccess != null) {
                        onCancelSuccess.run();
                    }
                } else {
                    showAlert("Failed to cancel registration.");
                }

                return;
            }

            // Register flow
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