package ser460.sundevilconnect.client.admin;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AdminControllerBase {

    protected boolean confirmAction(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .isPresent();
    }
}
