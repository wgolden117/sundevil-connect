package ser460.sundevilconnect.client.notifications;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class NotificationView {

    @FXML
    private ListView<NotificationItem> notificationList;

    @FXML
    private void initialize() {
        System.out.println("NotificationView initialized");

        notificationList.setItems(
                NotificationStore.getInstance().getNotifications()
        );

        // Render unread notifications as bold
        notificationList.setCellFactory(listView -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(NotificationItem item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.getMessage());

                    if (!item.isRead()) {
                        setStyle("-fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        // Double-click to mark as read
        notificationList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                NotificationItem selected = notificationList.getSelectionModel().getSelectedItem();

                if (selected != null) {
                    selected.markAsRead();
                    notificationList.refresh(); // update UI

                    // update badge
                    updateBadge();
                }
            }
        });
    }

    private void updateBadge() {
        var mainController = ser460.sundevilconnect.client.NavigationController
                .getInstance()
                .getMainController();

        if (mainController != null) {
            mainController.updateNotificationTabIndicator();
        }
    }
}