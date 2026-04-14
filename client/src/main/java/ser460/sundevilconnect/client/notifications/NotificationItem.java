package ser460.sundevilconnect.client.notifications;

public class NotificationItem {

    private final String message;
    private boolean isRead;

    public NotificationItem(String message) {
        this.message = message;
        this.isRead = false;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}