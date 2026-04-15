package ser460.sundevilconnect.server.admin;

import ser460.sundevilconnect.server.auth.User;
import ser460.sundevilconnect.server.core.DatabaseService;

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
                    "UPDATE content SET status = 'REMOVED' WHERE contentId = ?")) {
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

    public List<Content> getFlaggedContent() throws SQLException {
        List<Content> results = new ArrayList<>();
        String sql = """
                    SELECT c.*, u.email, u.firstName, u.lastName\s
                    FROM content c
                    JOIN users u ON c.createdBy = u.id
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

    private Content mapRow(ResultSet rs) throws SQLException {
        User user = new User(
                rs.getString("email"),
                rs.getString("firstName"),
                rs.getString("lastName"));
        Content content = new Content();
        content.setContentId(String.valueOf(rs.getInt("contentId")));
        content.setCreatedBy(user);
        content.setCreatedDate(LocalDate.parse(rs.getString("createdDate")));
        content.setStatus(rs.getString("status"));
        content.setFlagged(rs.getInt("isFlagged") == 1);
        content.setFlagReason(rs.getString("flagReason"));
        return content;
    }
}
