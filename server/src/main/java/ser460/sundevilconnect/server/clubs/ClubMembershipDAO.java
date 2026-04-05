package ser460.sundevilconnect.server.clubs;

import ser460.sundevilconnect.server.auth.Student;
import ser460.sundevilconnect.server.auth.UserDAO;
import ser460.sundevilconnect.server.core.DatabaseService;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                   c.clubId, c.name
                FROM clubMemberships cm
                JOIN users u ON cm.studentId = u.id
                JOIN clubs c ON cm.clubId = c.clubId
                WHERE cm.clubId = ?
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

    private ClubMembership mapRow(ResultSet rs) throws SQLException {
        Student student = new Student(
                String.valueOf(rs.getInt("userId")),
                rs.getString("firstName"),
                rs.getString("lastName"));
        Club club = new Club();
        club.setClubId(String.valueOf(rs.getInt("clubId")));
        club.setName(rs.getString("name"));

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
