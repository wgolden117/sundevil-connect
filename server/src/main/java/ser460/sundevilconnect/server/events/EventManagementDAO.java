package ser460.sundevilconnect.server.events;

import ser460.sundevilconnect.server.admin.ContentDAO;
import ser460.sundevilconnect.server.core.DatabaseService;
import ser460.sundevilconnect.shared.proto.EntitiesProto;
import ser460.sundevilconnect.shared.proto.EntitiesProto.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventManagementDAO {

    private final DatabaseService db;
    private final ContentDAO contentDAO;

    private static final Logger logger =
            Logger.getLogger(EventManagementDAO.class.getName());

    public EventManagementDAO(DatabaseService db, ContentDAO contentDAO) {
        this.db = db;
        this.contentDAO = contentDAO;
    }

    public boolean createEvent(Event event, int userId) throws SQLException {
        int contentId = contentDAO.createContent(userId);
        String sql = """
            INSERT INTO events (title, description, category, location, event_date, capacity, is_paid, hostedByClub, contentId)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (var conn = db.getConnection();
             var pstmt = conn.prepareStatement(sql)) {

            setEventFields(pstmt, event);
            pstmt.setInt(8, Integer.parseInt(event.getHostedBy().getClubId()));
            pstmt.setInt(9, contentId);

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

        try (var conn = db.getConnection();
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
        String sql = "UPDATE events SET status = 'CANCELLED' WHERE id = ?";

        try (var conn = db.getConnection();
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, eventId);
            return pstmt.executeUpdate() > 0;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to cancel eventId=" + eventId, e);
            return false;
        }
    }

    public Event getEventById(int eventId) {

        String sql = """
        SELECT e.id, e.title, e.description, e.category, e.location,
               e.event_date, e.capacity, e.is_paid,
               c.clubId, c.name, c.description as clubDescription,
               c.category as clubCategory, c.foundedDate, c.status as clubStatus
        FROM events e
        JOIN clubs c ON e.hostedByClub = c.clubId
        WHERE e.id = ?
    """;

        try (var conn = db.getConnection();
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, eventId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to get eventId=" + eventId, e);
        }

        return null;
    }

    public List<Event> getEventsForClub(String clubId) {
        String sql = """
            SELECT e.id, e.title, e.description, e.category, e.location,
                   e.event_date, e.capacity, e.is_paid,
                   c.clubId, c.name, c.description as clubDescription,
                   c.category as clubCategory, c.foundedDate, c.status as clubStatus
            FROM events e
            JOIN clubs c ON e.hostedByClub = c.clubId
            WHERE e.hostedByClub = ?
            AND (e.status IS NULL OR e.status != 'CANCELLED')
            ORDER BY e.event_date ASC
        """;

        List<Event> events = new ArrayList<>();

        try (var conn = db.getConnection();
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(clubId));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                events.add(mapRow(rs));
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to get events for clubId=" + clubId, e);
        }

        return events;
    }

    private Event mapRow(ResultSet rs) throws SQLException {
        EntitiesProto.Club club = EntitiesProto.Club.newBuilder()
                .setClubId(String.valueOf(rs.getInt("clubId")))
                .setName(rs.getString("name"))
                .setDescription(rs.getString("clubDescription"))
                .setCategory(rs.getString("clubCategory"))
                .setFoundedDate(rs.getString("foundedDate") != null ? rs.getString("foundedDate") : "")
                .setStatus(rs.getString("clubStatus"))
                .build();

        return Event.newBuilder()
                .setEventId(String.valueOf(rs.getInt("id")))
                .setTitle(rs.getString("title"))
                .setDescription(rs.getString("description") != null ? rs.getString("description") : "")
                .setCategory(rs.getString("category") != null ? rs.getString("category") : "")
                .setLocation(rs.getString("location") != null ? rs.getString("location") : "")
                .setEventDate(rs.getString("event_date") != null ? rs.getString("event_date") : "")
                .setCapacity(rs.getInt("capacity"))
                .setIsPaid(rs.getInt("is_paid") == 1)
                .setHostedBy(club)
                .build();
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