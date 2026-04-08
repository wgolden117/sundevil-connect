package ser460.sundevilconnect.server.events;

import ser460.sundevilconnect.server.core.DatabaseService;
import ser460.sundevilconnect.shared.proto.EntitiesProto.Event;

import java.util.logging.Level;
import java.util.logging.Logger;

public class EventManagementDAO {

    private final DatabaseService databaseService;
    private static final Logger logger =
            Logger.getLogger(EventManagementDAO.class.getName());

    public EventManagementDAO() {
        this.databaseService = DatabaseService.getInstance();
    }

    public boolean createEvent(Event event) {

        String sql = """
            INSERT INTO events (title, description, category, location, event_date, capacity, is_paid)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (var conn = databaseService.getConnection();
             var pstmt = conn.prepareStatement(sql)) {

            setEventFields(pstmt, event);

            pstmt.executeUpdate();
            return true;

        } catch (Exception e) {
            logger.log(Level.SEVERE,
                    "Failed to create event: " + event.getTitle(),
                    e);
            return false;
        }
    }

    public boolean updateEvent(Event event) {

        String sql = """
        UPDATE events
        SET title = ?, description = ?, category = ?, location = ?, event_date = ?, capacity = ?, is_paid = ?
        WHERE id = ?
    """;

        try (var conn = databaseService.getConnection();
             var pstmt = conn.prepareStatement(sql)) {

            setEventFields(pstmt, event);

            // eventId comes from proto as String
            int eventId;
            try {
                eventId = Integer.parseInt(event.getEventId());
            } catch (NumberFormatException e) {
                logger.log(Level.SEVERE, "Invalid eventId: " + event.getEventId(), e);
                return false;
            }

            pstmt.setInt(8, eventId);

            int rows = pstmt.executeUpdate();

            return rows > 0;

        } catch (Exception e) {
            logger.log(Level.SEVERE,
                    "Failed to update eventId=" + event.getEventId(),
                    e);
            return false;
        }
    }

    public boolean cancelEvent(int eventId) {

        String sql = "DELETE FROM events WHERE id = ?";

        try (var conn = databaseService.getConnection();
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, eventId);

            int rows = pstmt.executeUpdate();

            return rows > 0;

        } catch (Exception e) {
            logger.log(Level.SEVERE,
                    "Failed to cancel eventId=" + eventId,
                    e);
            return false;
        }
    }

    public java.util.List<Event> getEventsForClub(String clubId) {
        // TODO: implement once events table includes clubId
        return java.util.Collections.emptyList();
    }

    // helper methods
    private void setEventFields(java.sql.PreparedStatement pstmt, Event event) throws Exception {
        pstmt.setString(1, event.getTitle());
        pstmt.setString(2, event.getDescription());
        pstmt.setString(3, event.getCategory());
        pstmt.setString(4, event.getLocation());
        pstmt.setString(5, event.getEventDate());
        pstmt.setInt(6, event.getCapacity());
        pstmt.setInt(7, event.getIsPaid() ? 1 : 0);
    }
}