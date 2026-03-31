package ser460.sundevilconnect.server.core;

import ser460.sundevilconnect.server.auth.User;

public class AuthenticationService {
    private static AuthenticationService instance = new AuthenticationService();

    private AuthenticationService() {}

    public static AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    public User authenticateUser(String email, String password) {
        return DatabaseService.getInstance()
                .findUserByEmailAndPassword(email, password);
    }

    public String createSession(User user) {
        return "token_" + user.getUserId(); // simple placeholder
    }

    public boolean validateSession(String userId) { return false; }
    public void logout(String userId) {}
}
