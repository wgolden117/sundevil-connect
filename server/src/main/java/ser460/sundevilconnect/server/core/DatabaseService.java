package ser460.sundevilconnect.server.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import ser460.sundevilconnect.server.auth.User;
import ser460.sundevilconnect.shared.proto.EntitiesProto.*;

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
        try (Connection conn = getConnection()) {
            runScript("schema.sql", conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;

        String usersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                email TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                role TEXT NOT NULL,
                firstName TEXT,
                lastName TEXT
            );
        """;

        String studentsTable = """
            CREATE TABLE IF NOT EXISTS students (
                userId INTEGER PRIMARY KEY,
                major TEXT,
                graduationYear INTEGER,
                FOREIGN KEY (userId) REFERENCES users(id)
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

        String clubsTable = """
            CREATE TABLE IF NOT EXISTS clubs (
                clubId INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                description TEXT,
                category TEXT,
                foundedDate TEXT,
                status TEXT
            );
        """;

        String clubMembershipsTable = """
            CREATE TABLE IF NOT EXISTS clubMemberships (
                membershipId INTEGER PRIMARY KEY AUTOINCREMENT,
                studentId TEXT NOT NULL,
                clubId TEXT NOT NULL,
                role TEXT,
                joinDate TEXT,
                status TEXT,
                FOREIGN KEY (studentId) REFERENCES users(userId),
                FOREIGN KEY (clubId) REFERENCES clubs(clubId)
            );
        """;

        String clubMembershipRequestsTable = """
            CREATE TABLE IF NOT EXISTS membershipRequests (
                requestId TEXT PRIMARY KEY,
                studentId TEXT NOT NULL,
                clubId TEXT NOT NULL,
                status TEXT,
                requestDate TEXT,
                reviewedBy INTEGER,
                reviewDate TEXT,
                FOREIGN KEY (studentId) REFERENCES users(userId),
                FOREIGN KEY (clubId) REFERENCES clubs(clubId),
                FOREIGN KEY (reviewedBy) REFERENCES users(id)
            );
        """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(usersTable);
            stmt.execute(studentsTable);
            stmt.execute(eventsTable);
            stmt.execute(clubsTable);
            stmt.execute(clubMembershipsTable);
            stmt.execute(clubMembershipRequestsTable);

            System.out.println("Users + Events + Clubs tables ready");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();

        String sql = "SELECT * FROM events";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                events.add(
                        Event.newBuilder()
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
        String sql = "INSERT OR IGNORE INTO users (email, password, role, firstName, lastName) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // STUDENT
            pstmt.setString(1, "student@sundevil.com");
            pstmt.setString(2, "password123");
            pstmt.setString(3, "STUDENT");
            pstmt.setString(4, "Frank");
            pstmt.setString(5, "Castle");
            pstmt.executeUpdate();

            // CLUB LEADER
            pstmt.setString(1, "leader@sundevil.com");
            pstmt.setString(2, "password123");
            pstmt.setString(3, "CLUB_LEADER");
            pstmt.setString(4, "Peter");
            pstmt.setString(5, "Parker");
            pstmt.executeUpdate();

            // ADMIN
            pstmt.setString(1, "admin@sundevil.com");
            pstmt.setString(2, "password123");
            pstmt.setString(3, "ADMIN");
            pstmt.setString(4, "Matt");
            pstmt.setString(5, "Murdock");
            pstmt.executeUpdate();

            System.out.println("Test users inserted");

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
             Statement stmt = conn.createStatement();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {

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
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();


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

    private void runScript(String resourcePath, Connection conn) throws SQLException {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            String sql = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            for (String statement : sql.split(";")) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    conn.createStatement().execute(trimmed);
                }
            }
        } catch (IOException e) {
            throw new SQLException("Failed to load script: " + resourcePath, e);
        }
    }
}
