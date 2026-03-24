package ser460.sundevilconnect.server.auth;

public class AuthenticationController {
    // this acts as a socket-thread session controller that queries the AuthenticationService
    // on behalf of an incoming connection.

    public User authenticateUser(String username, String password) { return null; }
    public void logout(User user) {}
    public void resetPassword(String email) {}
    private boolean validateCredentials(String username, String password) { return false; }
}
