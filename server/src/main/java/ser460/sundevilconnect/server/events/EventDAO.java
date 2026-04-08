package ser460.sundevilconnect.server.events;

import java.util.logging.Level;
import ser460.sundevilconnect.server.core.DatabaseService;
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
        return databaseService.getAllEvents();
    }

    public List<Event> getEventsByCategory(String category) {

        List<Event> events = new ArrayList<>();

        String sql = "SELECT * FROM events WHERE category = ?";

        try (var conn = databaseService.getConnection();
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category);
            var rs = pstmt.executeQuery();

            while (rs.next()) {

                events.add(mapRowToEvent(rs));
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE,
                    "Failed to fetch events for category=" + category,
                    e);
        }
        return events;
    }

    public List<Event> searchEvents(String query) {

        List<Event> events = new ArrayList<>();

        String sql = """
        SELECT * FROM events
        WHERE LOWER(title) LIKE ? OR LOWER(description) LIKE ?
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
            logger.log(Level.SEVERE,
                    "Failed to search events with query=" + query,
                    e);
        }
        return events;
    }

    public Event getEventById(int eventId) {

        String sql = "SELECT * FROM events WHERE id = ?";

        try (var conn = databaseService.getConnection();
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, eventId);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRowToEvent(rs);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE,
                    "Failed to fetch event for eventId=" + eventId,
                    e);
        }

        return null;
    }

    // Helper methods
    private Event mapRowToEvent(java.sql.ResultSet rs) throws Exception {
        return Event.newBuilder()
                .setEventId(String.valueOf(rs.getInt("id")))
                .setTitle(rs.getString("title"))
                .setDescription(rs.getString("description"))
                .setCategory(rs.getString("category"))
                .setLocation(rs.getString("location"))
                .setEventDate(rs.getString("event_date"))
                .setCapacity(rs.getInt("capacity"))
                .setIsPaid(rs.getInt("is_paid") == 1)
                .build();
    }
}