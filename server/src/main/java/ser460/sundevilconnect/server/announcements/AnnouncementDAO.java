package ser460.sundevilconnect.server.announcements;

import ser460.sundevilconnect.server.admin.ContentDAO;
import ser460.sundevilconnect.server.auth.Student;
import ser460.sundevilconnect.server.auth.UserDAO;
import ser460.sundevilconnect.server.clubs.Club;
import ser460.sundevilconnect.server.clubs.ClubDAO;
import ser460.sundevilconnect.server.core.DatabaseService;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDAO {
    private final DatabaseService db;
    private final ContentDAO contentDAO;

    public AnnouncementDAO(DatabaseService db, ContentDAO contentDAO) {
        this.db = db;
        this.contentDAO = contentDAO;
    }

    public List<Announcement> getAnnouncementsForClub(int clubId, boolean includeDrafts) throws SQLException {
        List<Announcement> announcements = new ArrayList<>();
        String sql = """
                SELECT a.*,
                    u.id as userId, u.firstName, u.lastName,
                    c.clubId, c.name as clubName
                FROM announcements a
                JOIN users u ON a.createdBy = u.id
                JOIN clubs c ON a.postedToClub = c.clubId
                WHERE a.postedToClub = ? AND a.status != 'REMOVED'
                """ + (includeDrafts ? "" : "AND a.status = 'PUBLISHED'") +
                " ORDER BY a.postedDate DESC";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                announcements.add(mapRow(rs));
            }
        }
        return announcements;
    }

    public String createAnnouncement(int clubId, int createdBy, String title, String body, String status) throws SQLException {
        int contentId = contentDAO.createContent(createdBy);

        String sql = "INSERT INTO announcements (title, body, postedToClub, createdBy, status, postedDate, contentId) " +
                "VALUES  (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            ps.setString(1, title);
            ps.setString(2, body);
            ps.setInt(3, clubId);
            ps.setInt(4, createdBy);
            ps.setString(5, status);
            ps.setString(6, "PUBLISHED".equals(status)
                    ? LocalDate.now().toString()
                    : null);
            ps.setInt(7, contentId);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return String.valueOf(rs.getInt(1));
            }
        }
        throw new SQLException("Failed to create announcement");
    }

    public boolean editAnnouncement(int announcementId, String title, String body, String status) throws SQLException {
        String sql = "UPDATE announcements SET title = ?, body = ?, status = ?, postedDate = ? WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, title);
            ps.setString(2, body);
            ps.setString(3, status);
            ps.setString(4, "PUBLISHED".equals(status)
                    ? LocalDate.now().toString()
                    : null);
            ps.setInt(5, announcementId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteAnnouncement(int announcementId) throws SQLException {
        String sql = "UPDATE announcements SET status = 'REMOVED' WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, announcementId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean publishAnnouncement(int announcementId, boolean isPublished) throws SQLException {
        String sql = isPublished
                ? "UPDATE announcements SET status = 'PUBLISHED', postedDate = ? WHERE id = ?"
                : "UPDATE announcements SET status = 'DRAFT', postedDate = NULL WHERE id = ?";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (isPublished) {
                ps.setString(1, LocalDate.now().toString());
                ps.setInt(2, announcementId);
            } else {
                ps.setInt(1, announcementId);
            }
            return ps.executeUpdate() > 0;
        }
    }

    public Announcement getAnnouncementById(int announcementId) throws SQLException {
        String sql = """
        SELECT a.*,
            u.id as userId, u.firstName, u.lastName,
            c.clubId, c.name as clubName
        FROM announcements a
        JOIN users u ON a.createdBy = u.id
        JOIN clubs c ON a.postedToClub = c.clubId
        WHERE a.id = ?
    """;

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, announcementId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }
        }

        return null;
    }

    private Announcement mapRow(ResultSet rs) throws SQLException {
        Student createdBy = new Student(
                String.valueOf(rs.getInt("userId")),
                rs.getString("firstName"),
                rs.getString("lastName")
        );

        Club club = new Club();
        club.setClubId(String.valueOf(rs.getInt("clubId")));
        club.setName(rs.getString("clubName"));

        Announcement announcement = new Announcement();
        announcement.setAnnouncementId(String.valueOf(rs.getInt("id")));
        announcement.setTitle(rs.getString("title"));
        announcement.setBody(rs.getString("body"));
        String postedDateStr = rs.getString("postedDate");
        announcement.setPostedDate(postedDateStr != null ? LocalDate.parse(postedDateStr) : null);
        announcement.setPostedToClub(club);
        announcement.setCreatedBy(createdBy);
        announcement.setStatus(rs.getString("status"));
        return announcement;
    }

    public static EntitiesProto.Announcement toProto(Announcement announcement) {
        EntitiesProto.Announcement.Builder builder = EntitiesProto.Announcement.newBuilder()
                .setAnnouncementId(announcement.getAnnouncementId())
                .setPostedTo(ClubDAO.toProto(announcement.getPostedToClub()))
                .setCreatedBy(UserDAO.toProto(announcement.getCreatedBy()))
                .setStatus(announcement.getStatus());

        if (announcement.getTitle() != null) builder.setTitle(announcement.getTitle());
        if (announcement.getBody() != null) builder.setBody(announcement.getBody());
        if (announcement.getPostedDate() != null) builder.setPostedDate(announcement.getPostedDate().toString());

        return builder.build();
    }
}
