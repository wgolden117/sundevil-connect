package ser460.sundevilconnect.server.clubs;

import ser460.sundevilconnect.server.auth.Student;
import ser460.sundevilconnect.server.auth.User;
import ser460.sundevilconnect.server.auth.UserDAO;
import ser460.sundevilconnect.server.core.DatabaseService;
import ser460.sundevilconnect.shared.proto.ClubMembershipServiceProto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MembershipRequestDAO {
    private final DatabaseService db;

    public MembershipRequestDAO(DatabaseService db) {
        this.db = db;
    }

    // Note: We have to use the full reference path for protobuf MembershipRequest because of a naming conflict
    // between our entity and the proto definition. Pretty annoying, but I can't right now think of
    // a good naming distinction right now.

    public Optional<MembershipRequest> getRequestById(int requestId) throws SQLException {
        String sql = """
                SELECT mr.*,
                    s.id as userId, s.firstName, s.lastName,
                    c.clubId, c.name,
                    r.id as reviewerId, r.firstName as reviewerFirstName, r.lastName as reviewerLastName
                FROM membershipRequests mr
                JOIN users s ON mr.studentId = s.id
                JOIN clubs c ON mr.clubId = c.clubId
                LEFT JOIN users r ON mr.reviewedBy = r.id
                WHERE mr.requestId = ?
            """;
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    public List<MembershipRequest> getPendingRequestsForClub(int clubId) throws SQLException {
        List<MembershipRequest> requests = new ArrayList<>();

        String sql = """
                SELECT mr.*,
                    s.id as userId, s.firstName, s.lastName,
                    c.clubId, c.name,
                    r.id as reviewerId, r.firstName as reviewerFirstName, r.lastName as reviewerLastName
                FROM membershipRequests mr
                JOIN users s ON mr.studentId = s.id
                JOIN clubs c ON mr.clubId = c.clubId
                LEFT JOIN users r ON mr.reviewedBy = r.id
                WHERE mr.clubId = ? AND mr.status = 'PENDING'
                """;

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                requests.add(mapRow(rs));
            }
        }
        return requests;
    }

    public String createRequest(int studentId, int clubId) throws SQLException {
        String sql = "INSERT INTO membershipRequests (studentId, clubId, status, requestDate) " +
                "VALUES (?, ?, 'PENDING', ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, studentId);
            ps.setInt(2, clubId);
            ps.setString(3, LocalDate.now().toString());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return String.valueOf(rs.getInt(1));
            }
        }
        throw new SQLException("Failed to create membership request");
    }

    public boolean updateRequestStatus(int requestId, String status, int reviewerId) throws SQLException {
        String sql = "UPDATE membershipRequests SET status = ?, reviewedBy = ?, reviewDate = ? WHERE requestId = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, reviewerId);
            ps.setString(3, LocalDate.now().toString());
            ps.setInt(4, requestId);
            return ps.executeUpdate() > 0;
        }
    }

    private MembershipRequest mapRow(ResultSet rs) throws SQLException {
        Student student = new Student(
                String.valueOf(rs.getInt("userId")),
                rs.getString("firstName"),
                rs.getString("lastName")
        );

        Club club = new Club();
        club.setClubId(rs.getString("clubId"));
        club.setName(rs.getString("name"));

        MembershipRequest membershipRequest = new MembershipRequest();
        membershipRequest.setRequestId(String.valueOf(rs.getInt("requestId")));
        membershipRequest.setStudent(student);
        membershipRequest.setClub(club);
        membershipRequest.setStatus(rs.getString("status"));
        membershipRequest.setRequestDate(LocalDate.parse(rs.getString("requestDate")));

        // reviewedBy is nullable
        int reviewerId = rs.getInt("reviewerId");
        if (!rs.wasNull()) {
            User reviewer = new User(
                    String.valueOf(reviewerId),
                    rs.getString("reviewerFirstName"),
                    rs.getString("reviewerLastName")
            );
            membershipRequest.setReviewedBy(reviewer);
        }
        String reviewDate = rs.getString("reviewDate");
        if (!rs.wasNull())
            membershipRequest.setReviewDate(LocalDate.parse(reviewDate));

        return membershipRequest;
    }

    public static ClubMembershipServiceProto.MembershipRequest toProto(
            MembershipRequest request) {
        ClubMembershipServiceProto.MembershipRequest.Builder builder =
                ClubMembershipServiceProto.MembershipRequest.newBuilder()
                        .setRequestId(request.getRequestId())
                        .setStudent(UserDAO.toProto(request.getStudent()))
                        .setClub(ClubDAO.toProto(request.getClub()));

        if (request.getStatus() != null) builder.setStatus(request.getStatus());
        if (request.getRequestDate() != null) builder.setRequestDate(request.getRequestDate().toString());
        if (request.getReviewedBy() != null) builder.setReviewedBy(UserDAO.toProto(request.getReviewedBy()));
        if (request.getReviewDate() != null) builder.setReviewDate(request.getReviewDate().toString());

        return builder.build();
    }
}
