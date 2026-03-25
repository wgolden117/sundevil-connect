package ser460.sundevilconnect.shared.notifications;

import ser460.sundevilconnect.shared.proto.NotificationServiceProto.NotificationType;

import java.time.LocalDateTime;

public class Notification {
    private String notificationId;
    private String userId;
    private String message;
    private NotificationType type;
    private LocalDateTime timestamp;
    private boolean isRead;

    public void markAsRead() {}

    // Getters and Setters

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }
}
