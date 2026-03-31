package ser460.sundevilconnect.server.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import ser460.sundevilconnect.server.auth.User;
import ser460.sundevilconnect.shared.proto.EntitiesProto.Role;

public class DatabaseService {
    private static DatabaseService instance = new DatabaseService();

    private DatabaseService() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:sundevil.db");
            conn.close();
            System.out.println("SQLite connection successful");
        } catch (Exception e) {
            System.err.println("SQLite connection failed: " + e.getMessage());
        }
    }

    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    public Connection getConnection() throws Exception {
        return DriverManager.getConnection("jdbc:sqlite:sundevil.db");
    }

    public void initializeDatabase() {
        String usersTable = """
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            email TEXT NOT NULL UNIQUE,
            password TEXT NOT NULL,
            role TEXT NOT NULL
        );
    """;

        String eventsTable = """
        CREATE TABLE IF NOT EXISTS events (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            title TEXT NOT NULL,
            description TEXT,
            category TEXT,
            location TEXT,
            event_date TEXT,
            capacity INTEGER,
            is_paid INTEGER
        );
    """;

        try (Connection conn = getConnection();
             java.sql.Statement stmt = conn.createStatement()) {

            stmt.execute(usersTable);
            stmt.execute(eventsTable);

            System.out.println("Users + Events tables ready");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ser460.sundevilconnect.shared.proto.EntitiesProto.Event> getAllEvents() {
        List<ser460.sundevilconnect.shared.proto.EntitiesProto.Event> events = new java.util.ArrayList<>();

        String sql = "SELECT * FROM events";

        try (Connection conn = getConnection();
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                events.add(
                        ser460.sundevilconnect.shared.proto.EntitiesProto.Event.newBuilder()
                                .setEventId(String.valueOf(rs.getInt("id")))
                                .setTitle(rs.getString("title"))
                                .setDescription(rs.getString("description"))
                                .setCategory(rs.getString("category"))
                                .setLocation(rs.getString("location"))
                                .setEventDate(rs.getString("event_date"))
                                .setCapacity(rs.getInt("capacity"))
                                .setIsPaid(rs.getInt("is_paid") == 1)
                                .build()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return events;
    }

    // TEST METHODS
    public void insertTestUser() {
        String sql = "INSERT OR IGNORE INTO users (email, password, role) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "test@sundevil.com");
            pstmt.setString(2, "password123");
            pstmt.setString(3, "STUDENT");

            pstmt.executeUpdate();
            System.out.println("Test user inserted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertTestEvents() {

        String deleteSql = "DELETE FROM events";

        String insertSql = """
    INSERT INTO events (title, description, category, location, event_date, capacity, is_paid)
    VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = getConnection();
             java.sql.Statement stmt = conn.createStatement();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(insertSql)) {

            // clear old data
            stmt.executeUpdate(deleteSql);

            // Event 1
            pstmt.setString(1, "Tech Talk");
            pstmt.setString(2, "Learn about new tech trends");
            pstmt.setString(3, "Technology");
            pstmt.setString(4, "Room 101");
            pstmt.setString(5, "2026-04-10");
            pstmt.setInt(6, 100);
            pstmt.setInt(7, 0);
            pstmt.executeUpdate();

            // Event 2
            pstmt.setString(1, "Music Night");
            pstmt.setString(2, "Live performances");
            pstmt.setString(3, "Music");
            pstmt.setString(4, "Auditorium");
            pstmt.setString(5, "2026-04-12");
            pstmt.setInt(6, 200);
            pstmt.setInt(7, 1);
            pstmt.executeUpdate();

            System.out.println("Test events inserted");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User findUserByEmailAndPassword(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            java.sql.ResultSet rs = pstmt.executeQuery();


            if (rs.next()) {
                return new User(
                        String.valueOf(rs.getInt("id")),
                        rs.getString("email"),
                        Role.valueOf(rs.getString("role").toUpperCase())
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // TODO make sure we're accounting for multi-threaded access!!

    public void save(Object entity) {}
    public Object findBy(String id, Class type) { return null; }
    public List<Object> findAll(Class type) { return null; }
    public void update(Object entity) {}
    public void delete(String id, Class type) {}

    private String buildQuery(Class type) { return "";}
    private void executeUpdate(String query) {}
    private void executeQuery(String query) {}
}
