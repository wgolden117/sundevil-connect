package ser460.sundevilconnect.client.notifications;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class NotificationStore {

    private static final NotificationStore instance = new NotificationStore();

    private final ObservableList<String> notifications =
            FXCollections.observableArrayList();

    private NotificationStore() {}

    public static NotificationStore getInstance() {
        return instance;
    }

    public ObservableList<String> getNotifications() {
        return notifications;
    }

    public void addNotification(String message) {
        notifications.add(message);
    }
}