package ser460.sundevilconnect.server.clubs;

import ser460.sundevilconnect.server.auth.Student;
import ser460.sundevilconnect.server.auth.UserDAO;
import ser460.sundevilconnect.server.core.DatabaseService;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClubMembershipDAO {
    private final DatabaseService db;

    public ClubMembershipDAO(DatabaseService db) {
        this.db = db;
    }

    public List<ClubMembership> getMembersForClub(int clubId) throws SQLException {
        List<ClubMembership> members = new ArrayList<>();
        String sql = """
                SELECT cm.*,
                   u.id as userId, u.firstName, u.lastName,
                   c.clubId, c.name, c.description, c.category, c.foundedDate, c.status
                FROM clubMemberships cm
                JOIN users u ON cm.studentId = u.id
                JOIN clubs c ON cm.clubId = c.clubId
                WHERE cm.clubId = ? AND cm.status = 'ACTIVE'
                """;

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                members.add(mapRow(rs));
            }
        }
        return members;
    }

    public List<ClubMembership> getMembershipForStudent(int userId) throws SQLException {
        List<ClubMembership> memberships = new ArrayList<>();
        String sql = """
                    SELECT cm.*,
                        u.id as userId, u.firstName, u.lastName,
                        c.clubId, c.name, c.description, c.category, c.foundedDate, c.status
                    FROM clubMemberships cm
                    JOIN users u ON cm.studentId = u.id
                    JOIN clubs c ON cm.clubId = c.clubId
                    WHERE cm.studentId = ? AND cm.status = 'ACTIVE'
                """;

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                memberships.add(mapRow(rs));
            }
        }
        return memberships;
    }

    public String createMembership(int userId, int clubId) throws SQLException {
        String sql = "INSERT INTO clubMemberships (studentId, clubId, role, joinDate, status) " +
                "VALUES (?, ?, 'MEMBER', ?, 'ACTIVE')";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setInt(2, clubId);
            ps.setString(3, LocalDate.now().toString());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return String.valueOf(rs.getInt(1));
            }
        }
        throw new SQLException("Failed to create club membership");
    }

    public boolean removeMember(int membershipId) throws SQLException {
        String sql = "UPDATE clubMemberships SET status = 'REMOVED' WHERE membershipId = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, membershipId);
            return ps.executeUpdate() > 0;
        }
    }

    public String getMembershipStatus(int studentId, int clubId) throws SQLException {
        String sql = "SELECT status FROM clubMemberships WHERE studentId = ? AND clubId = ?";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, clubId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("status");
        }
        return null;
    }

    public boolean promoteMember(int membershipId) throws SQLException {
        String sql = "UPDATE clubMemberships SET role = 'LEADER' WHERE membershipId = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, membershipId);
            return ps.executeUpdate() > 0;
        }
    }

    private ClubMembership mapRow(ResultSet rs) throws SQLException {
        Student student = new Student(
                String.valueOf(rs.getInt("userId")),
                rs.getString("firstName"),
                rs.getString("lastName"));
        Club club = new Club();
        club.setClubId(String.valueOf(rs.getInt("clubId")));
        club.setName(rs.getString("name"));
        club.setDescription(rs.getString("description"));
        club.setCategory(rs.getString("category"));
        club.setStatus(rs.getString("status"));
        String foundedDate = rs.getString("foundedDate");
        if (foundedDate != null) club.setFoundedDate(LocalDate.parse(foundedDate));

        ClubMembership membership = new ClubMembership();
        membership.setMembershipId(String.valueOf(rs.getInt("membershipId")));
        membership.setStudent(student);
        membership.setClub(club);
        membership.setRole(rs.getString("role"));
        membership.setJoinDate(LocalDate.parse(rs.getString("joinDate")));
        membership.setStatus(rs.getString("status"));
        return membership;
    }

    public static EntitiesProto.ClubMembership toProto(ClubMembership membership) {
        EntitiesProto.ClubMembership.Builder builder = EntitiesProto.ClubMembership.newBuilder()
                .setMembershipId(membership.getMembershipId())
                .setStudent(UserDAO.toProto(membership.getStudent()))
                .setClub(ClubDAO.toProto(membership.getClub()));

        if (membership.getRole() != null) builder.setRole(membership.getRole());
        if (membership.getJoinDate() != null) builder.setJoinDate(membership.getJoinDate().toString());
        if (membership.getStatus() != null) builder.setStatus(membership.getStatus());

        return builder.build();
    }
}
