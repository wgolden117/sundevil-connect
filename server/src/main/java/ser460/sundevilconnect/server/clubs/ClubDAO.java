package ser460.sundevilconnect.server.clubs;

import ser460.sundevilconnect.server.core.DatabaseService;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClubDAO {
    private final DatabaseService db;

    public ClubDAO(DatabaseService db) {
        this.db = db;
    }

    public List<Club> getAllClubs() throws SQLException {
        List<Club> clubs = new ArrayList<>();
        String sql = "SELECT * FROM clubs WHERE status = 'ACTIVE'";

        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clubs.add(mapRow(rs));
            }
        }
        return clubs;
    }

    public List<Club> getClubsByCategory(String category) throws SQLException {
        List<Club> clubs = new ArrayList<>();
        String sql = "SELECT * FROM clubs WHERE category = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                clubs.add(mapRow(rs));
            }
        }
        return clubs;
    }

    public List<Club> searchClubs(String keyword) throws SQLException {
        List<Club> clubs = new ArrayList<>();
        String sql = "SELECT * FROM clubs WHERE name LIKE ? OR description LIKE ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                clubs.add(mapRow(rs));
            }
        }
        return clubs;
    }

    public Optional<Club> getClubById(int id) throws SQLException {
        String sql = "SELECT * FROM clubs WHERE clubId = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    public List<Club> getPendingClubs() throws SQLException {
        List<Club> clubs = new ArrayList<>();
        String sql = "SELECT * FROM clubs WHERE status = 'PENDING'";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clubs.add(mapRow(rs));
            }
        }
        return clubs;
    }

    public void insertClub(Club club, int submittedBy) throws SQLException {
        String sql = """
            INSERT INTO clubs (name, description, category, status, submittedBy)
            VALUES (?, ?, ?, 'PENDING', ?)
            """;
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, club.getName());
            stmt.setString(2, club.getDescription());
            stmt.setString(3, club.getCategory());
            stmt.setInt(4, submittedBy);
            stmt.executeUpdate();
        }
    }

    public boolean approveClub(int clubId) throws SQLException {
        String sql = "UPDATE clubs SET status = 'ACTIVE', foundedDate = ? WHERE clubId = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, LocalDate.now().toString());
            stmt.setInt(2, clubId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean rejectClub(int clubId) throws SQLException {
        String sql = "UPDATE clubs SET status = 'REJECTED' WHERE clubId = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            return stmt.executeUpdate() > 0;
        }
    }

    public int getSubmittedByForClub(int clubId) throws SQLException {
        String query = "SELECT submittedBy FROM clubs WHERE clubId = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, clubId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("submittedBy");
            throw new SQLException("Club not found after approval: " + clubId);
        }
    }

    private Club mapRow(ResultSet rs) throws SQLException {
        Club club = new Club();
        club.setClubId(String.valueOf(rs.getInt("clubId")));
        club.setName(rs.getString("name"));
        club.setDescription(rs.getString("description"));
        club.setCategory(rs.getString("category"));
        club.setStatus(rs.getString("status"));
        String date = rs.getString("foundedDate");
        club.setFoundedDate(date != null ? LocalDate.parse(date) : null);
        return club;
    }

    public static EntitiesProto.Club toProto(Club club) {
        EntitiesProto.Club.Builder builder = EntitiesProto.Club.newBuilder()
                .setClubId(club.getClubId())
                .setName(club.getName());

        if (club.getDescription() != null) builder.setDescription(club.getDescription());
        if (club.getCategory() != null) builder.setCategory(club.getCategory());
        if (club.getStatus() != null) builder.setStatus(club.getStatus());
        if (club.getFoundedDate() != null) builder.setFoundedDate(club.getFoundedDate().toString());

        return builder.build();
    }
}
