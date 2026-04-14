package ser460.sundevilconnect.client.notifications;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class NotificationView {

    @FXML
    private ListView<String> notificationList;

    @FXML
    private void initialize() {
        System.out.println("NotificationView initialized");

        notificationList.setItems(
                NotificationStore.getInstance().getNotifications()
        );
    }
}