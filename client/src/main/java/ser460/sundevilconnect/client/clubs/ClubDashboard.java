package ser460.sundevilconnect.client.clubs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class ClubDashboard {
    @FXML private Label clubNameLabel;
    @FXML private Button requestsButton;
    @FXML private Button eventsButton;
    @FXML private Button announcementsButton;
    @FXML private AnchorPane contentArea;

    @FXML
    private void initialize() {
        // TODO: called by JavaFX after FXML loads, fetch initial data from server
    }

    @FXML
    private void onRequestsClicked(ActionEvent actionEvent) {
    }

    @FXML
    private void onEventsClicked(ActionEvent actionEvent) {
    }

    @FXML
    private void onAnnouncementsClicked(ActionEvent actionEvent) {
    }
}
