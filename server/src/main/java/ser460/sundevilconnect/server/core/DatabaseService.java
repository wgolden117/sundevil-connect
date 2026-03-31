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
        String sql = """
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            email TEXT NOT NULL UNIQUE,
            password TEXT NOT NULL,
            role TEXT NOT NULL
        );
    """;

        try (Connection conn = getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Users table ready");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
