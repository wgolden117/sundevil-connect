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
        String sql = "SELECT * FROM clubs";

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

    private Club mapRow(ResultSet rs) throws SQLException {
        Club club = new Club();
        club.setClubId(String.valueOf(rs.getInt("clubId")));
        club.setName(rs.getString("name"));
        club.setDescription(rs.getString("description"));
        club.setCategory(rs.getString("category"));
        club.setStatus(rs.getString("status"));
        club.setFoundedDate(LocalDate.parse(rs.getString("foundedDate")));
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
