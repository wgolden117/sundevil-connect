package ser460.sundevilconnect.server.events;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ser460.sundevilconnect.server.core.DatabaseService;
import ser460.sundevilconnect.shared.proto.EntitiesProto.Event;
import ser460.sundevilconnect.shared.proto.EntitiesProto.UserSummary;
import ser460.sundevilconnect.shared.proto.EventRegistrationServiceProto.EventRegistration;

public class EventRegistrationDAO {

    private final DatabaseService databaseService;
    private static final Logger logger =
            Logger.getLogger(EventRegistrationDAO.class.getName());

    public EventRegistrationDAO() {
        this.databaseService = DatabaseService.getInstance();
    }

    public boolean isEventAtCapacity(int eventId) {

        String countSql = "SELECT COUNT(*) FROM eventRegistrations WHERE eventId = ?";
        String capacitySql = "SELECT capacity FROM events WHERE id = ?";

        try (var conn = databaseService.getConnection()) {

            int count = 0;
            int capacity = 0;

            try (var pstmt = conn.prepareStatement(countSql)) {
                pstmt.setInt(1, eventId);
                var rs = pstmt.executeQuery();
                if (rs.next()) count = rs.getInt(1);
            }

            try (var pstmt = conn.prepareStatement(capacitySql)) {
                pstmt.setInt(1, eventId);
                var rs = pstmt.executeQuery();
                if (rs.next()) capacity = rs.getInt(1);
            }

            return count >= capacity;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Capacity check failed", e);
            return false;
        }
    }

    public boolean isStudentAlreadyRegistered(int studentId, int eventId) {

        String sql = "SELECT COUNT(*) FROM eventRegistrations WHERE studentId = ? AND eventId = ?";

        try (var conn = databaseService.getConnection();
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, eventId);

            var rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

        } catch (Exception e) {
            logger.log(Level.SEVERE,
                    "Failed to check existing registration for studentId=" + studentId +
                            ", eventId=" + eventId,
                    e);
            return false;
        }
    }

    public boolean registerStudent(int studentId, int eventId, long registrationTime) {

        String insertSql = "INSERT INTO eventRegistrations (studentId, eventId, registrationDate) VALUES (?, ?, ?)";

        try (var conn = databaseService.getConnection();
             var pstmt = conn.prepareStatement(insertSql)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, eventId);
            pstmt.setString(3, String.valueOf(registrationTime));

            pstmt.executeUpdate();
            return true;

        } catch (Exception e) {
            logger.log(Level.SEVERE,
                    "Failed to register studentId=" + studentId +
                            " for eventId=" + eventId +
                            " at time=" + registrationTime,
                    e);
            return false;
        }
    }

    public boolean cancelRegistration(int registrationId) {

        String sql = "DELETE FROM eventRegistrations WHERE registrationId = ?";

        try (var conn = databaseService.getConnection();
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, registrationId);
            int rows = pstmt.executeUpdate();

            return rows > 0;

        } catch (Exception e) {
            logger.log(Level.SEVERE,
                    "Failed to cancel registrationId=" + registrationId,
                    e);
            return false;
        }
    }

    public List<EventRegistration> getRegistrationsForStudent(int studentId) {

        List<EventRegistration> registrations = new ArrayList<>();

        String sql = """
        SELECT er.registrationId, er.registrationDate,
               e.*, u.firstName, u.lastName
        FROM eventRegistrations er
        JOIN events e ON er.eventId = e.id
        JOIN users u ON er.studentId = u.id
        WHERE er.studentId = ?
    """;

        try (var conn = databaseService.getConnection();
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            var rs = pstmt.executeQuery();

            while (rs.next()) {

                var event = Event.newBuilder()
                        .setEventId(String.valueOf(rs.getInt("id")))
                        .setTitle(rs.getString("title"))
                        .setDescription(rs.getString("description"))
                        .setCategory(rs.getString("category"))
                        .setLocation(rs.getString("location"))
                        .setEventDate(rs.getString("event_date"))
                        .setCapacity(rs.getInt("capacity"))
                        .setIsPaid(rs.getInt("is_paid") == 1)
                        .build();

                var student = UserSummary.newBuilder()
                        .setUserId(String.valueOf(studentId))
                        .setDisplayName(
                                rs.getString("firstName") + " " + rs.getString("lastName")
                        )
                        .build();

                var registration = EventRegistration.newBuilder()
                        .setRegistrationId(String.valueOf(rs.getInt("registrationId")))
                        .setEvent(event)
                        .setStudent(student)
                        .setRegistrationDate(rs.getString("registrationDate"))
                        .setStatus("ACTIVE")
                        .build();

                registrations.add(registration);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE,
                    "Failed to fetch registrations for studentId=" + studentId,
                    e);
        }

        return registrations;
    }

    public List<EventRegistration> getRegistrationsForEvent(int eventId) {

        List<EventRegistration> registrations = new ArrayList<>();

        String sql = """
        SELECT er.registrationId, er.registrationDate,
               u.firstName, u.lastName, u.id as studentId,
               e.*
        FROM eventRegistrations er
        JOIN users u ON er.studentId = u.id
        JOIN events e ON er.eventId = e.id
        WHERE er.eventId = ?
    """;

        try (var conn = databaseService.getConnection();
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, eventId);
            var rs = pstmt.executeQuery();

            while (rs.next()) {

                var event = Event.newBuilder()
                        .setEventId(String.valueOf(rs.getInt("id")))
                        .setTitle(rs.getString("title"))
                        .setDescription(rs.getString("description"))
                        .setCategory(rs.getString("category"))
                        .setLocation(rs.getString("location"))
                        .setEventDate(rs.getString("event_date"))
                        .setCapacity(rs.getInt("capacity"))
                        .setIsPaid(rs.getInt("is_paid") == 1)
                        .build();

                var student = UserSummary.newBuilder()
                        .setUserId(String.valueOf(rs.getInt("studentId")))
                        .setDisplayName(
                                rs.getString("firstName") + " " + rs.getString("lastName")
                        )
                        .build();

                var registration = EventRegistration.newBuilder()
                        .setRegistrationId(String.valueOf(rs.getInt("registrationId")))
                        .setEvent(event)
                        .setStudent(student)
                        .setRegistrationDate(rs.getString("registrationDate"))
                        .setStatus("ACTIVE")
                        .build();

                registrations.add(registration);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE,
                    "Failed to fetch registrations for eventId=" + eventId,
                    e);
        }

        return registrations;
    }
}