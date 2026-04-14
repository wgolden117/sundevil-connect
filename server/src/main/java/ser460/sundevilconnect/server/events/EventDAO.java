package ser460.sundevilconnect.server.events;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import ser460.sundevilconnect.server.core.DatabaseService;
import ser460.sundevilconnect.shared.proto.EntitiesProto;
import ser460.sundevilconnect.shared.proto.EntitiesProto.Event;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;

public class EventDAO {

    private final DatabaseService databaseService;
    private static final Logger logger =
            Logger.getLogger(EventDAO.class.getName());

    public EventDAO() {
        this.databaseService = DatabaseService.getInstance();
    }

    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();

        String sql = """
            SELECT e.*, c.clubId, c.name as clubName, c.description as clubDescription,
                   c.category as clubCategory, c.foundedDate, c.status as clubStatus
            FROM events e
            LEFT JOIN clubs c ON e.hostedByClub = c.clubId
            WHERE (e.status IS NULL OR e.status != 'CANCELLED')
        """;

        try (Connection conn = databaseService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                events.add(mapRowToEvent(rs));
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to fetch all events", e);
        }

        return events;
    }

    public List<Event> getEventsByCategory(String category) {
        List<Event> events = new ArrayList<>();

        String sql = """
                SELECT e.*, c.clubId, c.name as clubName, c.description as clubDescription,
                       c.category as clubCategory, c.foundedDate, c.status as clubStatus
                FROM events e
                LEFT JOIN clubs c ON e.hostedByClub = c.clubId
                WHERE e.category = ? AND (e.status IS NULL OR e.status != 'CANCELLED')
            """;

        try (var conn = databaseService.getConnection();
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category);
            var rs = pstmt.executeQuery();
            while (rs.next()) {
                events.add(mapRowToEvent(rs));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to fetch events for category=" + category, e);
        }
        return events;
    }

    public List<Event> searchEvents(String query) {
        List<Event> events = new ArrayList<>();

        String sql = """
                    SELECT e.*, c.clubId, c.name as clubName, c.description as clubDescription,
                           c.category as clubCategory, c.foundedDate, c.status as clubStatus
                    FROM events e
                    LEFT JOIN clubs c ON e.hostedByClub = c.clubId
                    WHERE LOWER(e.title) LIKE ? OR LOWER(e.description) LIKE ?
                                       AND (e.status IS NULL OR e.status != 'CANCELLED')
                """;

        try (var conn = databaseService.getConnection();
             var pstmt = conn.prepareStatement(sql)) {
            String searchTerm = "%" + query.toLowerCase() + "%";
            pstmt.setString(1, searchTerm);
            pstmt.setString(2, searchTerm);
            var rs = pstmt.executeQuery();
            while (rs.next()) {
                events.add(mapRowToEvent(rs));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to search events with query=" + query, e);
        }
        return events;
    }

    public Event getEventById(int eventId) {
        String sql = """
                SELECT e.*, c.clubId, c.name as clubName, c.description as clubDescription,
                       c.category as clubCategory, c.foundedDate, c.status as clubStatus
                FROM events e
                LEFT JOIN clubs c ON e.hostedByClub = c.clubId
                WHERE e.id = ? AND (e.status IS NULL OR e.status != 'CANCELLED')
            """;

        try (var conn = databaseService.getConnection();
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRowToEvent(rs);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to fetch event for eventId=" + eventId, e);
        }
        return null;
    }

    // Helper methods
    private Event mapRowToEvent(ResultSet rs) throws Exception {
        Event.Builder builder = Event.newBuilder()
                .setEventId(String.valueOf(rs.getInt("id")))
                .setTitle(rs.getString("title"))
                .setDescription(rs.getString("description"))
                .setCategory(rs.getString("category"))
                .setLocation(rs.getString("location"))
                .setEventDate(rs.getString("event_date"))
                .setCapacity(rs.getInt("capacity"))
                .setIsPaid(rs.getInt("is_paid") == 1);

        int clubId = rs.getInt("clubId");
        if (!rs.wasNull()) {
            builder.setHostedBy(
                    EntitiesProto.Club.newBuilder()
                            .setClubId(String.valueOf(clubId))
                            .setName(rs.getString("clubName"))
                            .setDescription(rs.getString("clubDescription"))
                            .setCategory(rs.getString("clubCategory"))
                            .setFoundedDate(rs.getString("foundedDate"))
                            .setStatus(rs.getString("clubStatus"))
                            .build()
            );
        }

        return builder.build();
    }
}