package ser460.sundevilconnect.server.core;

import java.util.List;

public class DatabaseService {
    private static DatabaseService instance = new DatabaseService();

    private DatabaseService() {}

    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
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
