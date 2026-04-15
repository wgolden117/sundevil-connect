package ser460.sundevilconnect.server.admin;

import ser460.sundevilconnect.server.core.DatabaseService;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContentDAO {
    private final DatabaseService db;

    public ContentDAO(DatabaseService db) {
        this.db = db;
    }

    public int createContent(int userId) throws SQLException {
        String sql = "INSERT INTO content (createdBy, createdDate) VALUES (?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userId);
            stmt.setString(2, LocalDate.now().toString());
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
            throw new SQLException("Failed to retrieve generated contentId");
        }
    }

    public void flagContent(int contentId, String reason) throws SQLException {
        String sql = "UPDATE content SET isFlagged = 1, flagReason = ? WHERE contentId = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reason);
            stmt.setInt(2, contentId);
            stmt.executeUpdate();
        }
    }

    public void approveContent(int contentId) throws SQLException {
        String sql = "UPDATE content SET isFlagged = 0, flagReason = NULL WHERE contentId = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, contentId);
            stmt.executeUpdate();
        }
    }

    public void removeContent(int contentId) throws SQLException {
        try (Connection conn = db.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE content SET status = 'REMOVED', isFlagged = 0 WHERE contentId = ?")) {
                stmt.setInt(1, contentId);
                stmt.executeUpdate();
            }
            // set the status of either announcement or event to removed
            // its safe to run this through update, since only one of these will actually have
            // the right content id, so one will just be a NOP, which is safe
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE events SET status = 'CANCELLED' WHERE contentId = ?")) {
                stmt.setInt(1, contentId);
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE announcements SET status = 'REMOVED' WHERE contentId = ?")) {
                stmt.setInt(1, contentId);
                stmt.executeUpdate();
            }
        }
    }

    public EntitiesProto.Content getContentById(int contentId) throws SQLException {
        String sql = """
                SELECT c.*, u.id as userId, u.email, u.firstName, u.lastName,
                       e.id as eventId, e.title as eventTitle, e.description as eventDescription,
                       e.category, e.location, e.event_date, e.capacity, e.is_paid,
                       a.id as announcementId, a.title as announcementTitle, a.body
                FROM content c
                JOIN users u ON c.createdBy = u.id
                LEFT JOIN events e ON e.contentId = c.contentId
                LEFT JOIN announcements a ON a.contentId = c.contentId
                WHERE c.contentId = ?
            """;
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, contentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
            throw new SQLException("Content not found: " + contentId);
        }
    }

    public List<EntitiesProto.Content> getFlaggedContent() throws SQLException {
        List<EntitiesProto.Content> results = new ArrayList<>();
        String sql = """
                SELECT c.*, u.id as userId, u.email, u.firstName, u.lastName,
                       e.id as eventId, e.title as eventTitle, e.description as eventDescription,
                       e.category, e.location, e.event_date, e.capacity, e.is_paid,
                       a.id as announcementId, a.title as announcementTitle, a.body
                FROM content c
                JOIN users u ON c.createdBy = u.id
                LEFT JOIN events e ON e.contentId = c.contentId
                LEFT JOIN announcements a ON a.contentId = c.contentId
                WHERE c.isFlagged = 1
            """;
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        }
        return results;
    }

    private EntitiesProto.Content mapRow(ResultSet rs) throws SQLException {
        EntitiesProto.UserSummary createdBy = EntitiesProto.UserSummary.newBuilder()
                .setUserId(String.valueOf(rs.getInt("userId")))
                .setDisplayName(rs.getString("firstName") + " " + rs.getString("lastName"))
                .build();

        EntitiesProto.Content.Builder builder = EntitiesProto.Content.newBuilder()
                .setContentId(String.valueOf(rs.getInt("contentId")))
                .setCreatedBy(createdBy)
                .setStatus(rs.getString("status"))
                .setIsFlagged(rs.getInt("isFlagged") == 1)
                .setFlagReason(rs.getString("flagReason") != null ? rs.getString("flagReason") : "");

        int eventId = rs.getInt("eventId");
        if (!rs.wasNull()) {
            builder.setEvent(EntitiesProto.Event.newBuilder()
                    .setEventId(String.valueOf(eventId))
                    .setTitle(rs.getString("eventTitle"))
                    .setDescription(rs.getString("eventDescription") != null ? rs.getString("eventDescription") : "")
                    .setCategory(rs.getString("category") != null ? rs.getString("category") : "")
                    .setLocation(rs.getString("location") != null ? rs.getString("location") : "")
                    .setEventDate(rs.getString("event_date") != null ? rs.getString("event_date") : "")
                    .setCapacity(rs.getInt("capacity"))
                    .setIsPaid(rs.getInt("is_paid") == 1)
                    .build());
        } else {
            builder.setAnnouncement(EntitiesProto.Announcement.newBuilder()
                    .setAnnouncementId(String.valueOf(rs.getInt("announcementId")))
                    .setTitle(rs.getString("announcementTitle") != null ? rs.getString("announcementTitle") : "")
                    .setBody(rs.getString("body") != null ? rs.getString("body") : "")
                    .build());
        }

        return builder.build();
    }
}
