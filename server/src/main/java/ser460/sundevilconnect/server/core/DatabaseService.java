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

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:sundevil.db");
    }

    public void initializeDatabase() {
        try (Connection conn = getConnection()) {
            runScript("/schema.sql", conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Database initialization completed");
    }

    public void insertTestData() {
        try (Connection conn = getConnection())  {
            runScript("/seed.sql", conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Seed data inserted into database");
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
        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            assert inputStream != null;
            String sql = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
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
