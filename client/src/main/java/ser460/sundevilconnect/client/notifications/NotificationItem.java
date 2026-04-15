package ser460.sundevilconnect.client.notifications;

public class NotificationItem {

    private final String message;
    private boolean isRead;
    private boolean selected = false;

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

    public void setRead(boolean read) {
        this.isRead = read;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}