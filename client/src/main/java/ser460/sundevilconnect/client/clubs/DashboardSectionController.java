package ser460.sundevilconnect.client.clubs;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

public abstract class DashboardSectionController {

    public abstract void setClub(EntitiesProto.Club club);

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
