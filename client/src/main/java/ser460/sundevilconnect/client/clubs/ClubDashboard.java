package ser460.sundevilconnect.client.clubs;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

public class ClubDashboard {
    @FXML private VBox root;
    @FXML private Label clubNameLabel;
    @FXML private AnchorPane contentArea;

    private EntitiesProto.Club club;

    public void setClub(EntitiesProto.Club club) {
        this.club = club;
        clubNameLabel.setText(club.getName());
    }

    @FXML
    private void onRequestsClicked() {
        loadSection("/fxml/clubs/dashboard_membership.fxml");
    }

    @FXML
    private void onEventsClicked() {
        loadSection("/fxml/events/dashboard_events.fxml");
    }

    @FXML
    private void onAnnouncementsClicked() {
        loadSection("/fxml/announcements/dashboard_announcements.fxml");
    }

    private void loadSection(String fxmlPath) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent section = fxmlLoader.load();
            DashboardSectionController controller = fxmlLoader.getController();
            controller.setClub(club);
            AnchorPane.setTopAnchor(section, 0.0);
            AnchorPane.setBottomAnchor(section, 0.0);
            AnchorPane.setLeftAnchor(section, 0.0);
            AnchorPane.setRightAnchor(section, 0.0);
            contentArea.getChildren().setAll(section);
        } catch (Exception e) {
            System.err.println("Failed to load section: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
