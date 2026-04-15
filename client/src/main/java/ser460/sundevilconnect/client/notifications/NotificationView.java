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

        // Render notifications with checkbox + bold unread
        notificationList.setCellFactory(listView -> new javafx.scene.control.ListCell<>() {

            private final javafx.scene.control.CheckBox checkBox = new javafx.scene.control.CheckBox();

            @Override
            protected void updateItem(NotificationItem item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {

                    javafx.scene.layout.HBox row = new javafx.scene.layout.HBox();
                    row.setSpacing(10);
                    row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                    javafx.scene.control.Label label =
                            new javafx.scene.control.Label(item.getMessage());

                    javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
                    javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

                    // checkbox behavior
                    checkBox.setSelected(item.isSelected());
                    checkBox.setOnAction(e ->
                            item.setSelected(checkBox.isSelected())
                    );

                    // bold unread
                    if (!item.isRead()) {
                        label.setStyle("-fx-font-weight: bold;");
                    } else {
                        label.setStyle("");
                    }

                    row.getChildren().addAll(label, spacer, checkBox);

                    setGraphic(row);
                    setText(null);
                }
            }
        });

        // Double-click to mark as read
        notificationList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                NotificationItem selected =
                        notificationList.getSelectionModel().getSelectedItem();

                if (selected != null) {
                    selected.markAsRead();
                    notificationList.refresh();
                    updateBadge();
                }
            }
        });
    }

    @FXML
    private void handleSelectAll() {
        NotificationStore.getInstance().getNotifications()
                .forEach(n -> n.setSelected(true));

        notificationList.refresh();
    }

    @FXML
    private void handleDeselectAll() {
        NotificationStore.getInstance().getNotifications()
                .forEach(n -> n.setSelected(false));

        notificationList.refresh();
    }

    @FXML
    private void handleDeleteSelected() {
        NotificationStore.getInstance().getNotifications()
                .removeIf(NotificationItem::isSelected);

        notificationList.refresh();
        updateBadge();
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