package ser460.sundevilconnect.server.core;

import ser460.sundevilconnect.server.auth.User;

public class AuthenticationService {
    private static AuthenticationService instance;

    private AuthenticationService() {}

    public static AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    public User authenticateUser(String email, String password) { return null; }
    public boolean validateSession(String userId) { return false; }
    public String createSession(User user) { return null; }
    public void logout(String userId) {}
}
