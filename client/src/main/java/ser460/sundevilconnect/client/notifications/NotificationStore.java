package ser460.sundevilconnect.client.notifications;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class NotificationStore {

    private static final NotificationStore instance = new NotificationStore();

    private final ObservableList<NotificationItem> notifications =
            FXCollections.observableArrayList();

    private NotificationStore() {}

    public static NotificationStore getInstance() {
        return instance;
    }

    public ObservableList<NotificationItem> getNotifications() {
        return notifications;
    }

    public void addNotification(String notificationId, String message) {
        notifications.add(new NotificationItem(notificationId, message));
    }

    public boolean hasUnread() {
        return notifications.stream().anyMatch(n -> !n.isRead());
    }

    public void markAllAsRead() {
        notifications.forEach(NotificationItem::markAsRead);
    }

    public int getUnreadCount() {
        int count = 0;

        for (NotificationItem item : notifications) {
            if (!item.isRead()) {
                count++;
            }
        }

        return count;
    }
}