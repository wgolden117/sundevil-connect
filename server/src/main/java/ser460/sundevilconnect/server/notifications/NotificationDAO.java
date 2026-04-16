package ser460.sundevilconnect.server.notifications;

import ser460.sundevilconnect.server.core.DatabaseService;
import ser460.sundevilconnect.shared.proto.NotificationServiceProto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    private final DatabaseService db;

    public NotificationDAO(DatabaseService db) {
        this.db = db;
    }

    // --------------------------------------------
    // SAVE notification to DB
    // --------------------------------------------
    public void saveNotification(NotificationServiceProto.NotificationMessage notification) {
        String sql = """
            INSERT INTO notifications (notificationId, userId, message, type, timestamp, isRead)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, notification.getNotificationId());
            ps.setString(2, notification.getUserId());
            ps.setString(3, notification.getMessage());
            ps.setString(4, notification.getType().name());
            ps.setString(5, notification.getTimestamp());
            ps.setInt(6, notification.getIsRead() ? 1 : 0);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --------------------------------------------
    // GET notifications for a user
    // --------------------------------------------
    public List<NotificationServiceProto.NotificationMessage> getNotificationsForUser(String userId) {

        List<NotificationServiceProto.NotificationMessage> notifications = new ArrayList<>();

        String sql = """
            SELECT * FROM notifications
            WHERE userId = ? AND isRead = 0
            ORDER BY timestamp DESC
        """;

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                NotificationServiceProto.NotificationMessage notification =
                        NotificationServiceProto.NotificationMessage.newBuilder()
                                .setNotificationId(rs.getString("notificationId"))
                                .setUserId(rs.getString("userId"))
                                .setMessage(rs.getString("message"))
                                .setType(NotificationServiceProto.NotificationType.valueOf(rs.getString("type")))
                                .setTimestamp(rs.getString("timestamp"))
                                .setIsRead(rs.getInt("isRead") == 1)
                                .build();

                notifications.add(notification);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return notifications;
    }

    public void markAsRead(String notificationId) {
        String sql = "UPDATE notifications SET isRead = 1 WHERE notificationId = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, notificationId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}