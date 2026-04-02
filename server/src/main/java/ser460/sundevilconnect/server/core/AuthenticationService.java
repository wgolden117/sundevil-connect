package ser460.sundevilconnect.server.core;

import ser460.sundevilconnect.server.auth.User;

import java.util.HashMap;

public class AuthenticationService {
    private static AuthenticationService instance = new AuthenticationService();

    private HashMap<String, User> loggedInUsers = new HashMap<String, User>();

    private AuthenticationService() {}

    public static AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    public String authenticateUser(String email, String password) {
        User user = DatabaseService.getInstance()
                .findUserByEmailAndPassword(email, password);
        if (user != null) {
            String sessionToken = createSession(user);
            if (!loggedInUsers.containsKey(sessionToken)) {
                loggedInUsers.put(sessionToken, user);
                return sessionToken;
            }
        }
        return null;
    }

    private String createSession(User user) {
        return "token_" + user.getUserId(); // simple placeholder
    }

    private boolean endSession(String sessionToken) {
        User user = loggedInUsers.remove(sessionToken);
        return user != null;
    }

    public User getSessionUser(String sessionToken) {
        return loggedInUsers.get(sessionToken);
    }

    public boolean validateSession(String sessionToken) {
        return loggedInUsers.containsKey(sessionToken);
    }

    public boolean logout(String sessionToken) {
        return endSession(sessionToken);
    }
}
