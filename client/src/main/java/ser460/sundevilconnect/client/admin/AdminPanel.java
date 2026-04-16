package ser460.sundevilconnect.client.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class AdminPanel {
    @FXML private VBox root;
    @FXML private AnchorPane contentArea;

    @FXML
    private void onClubsClicked() {
        loadSection("/fxml/admin/admin_clubs.fxml");
    }

    @FXML
    private void onContentClicked() {
        loadSection("/fxml/admin/admin_content.fxml");
    }

    private void loadSection(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent section = loader.load();
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
