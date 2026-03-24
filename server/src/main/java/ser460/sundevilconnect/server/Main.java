package ser460.sundevilconnect.server;

import ser460.sundevilconnect.server.core.AuthenticationService;
import ser460.sundevilconnect.server.core.DatabaseService;
import ser460.sundevilconnect.server.core.NotificationService;

public class Main {
    public static void main(String[] args) {
        // create and init singleton services
        DatabaseService.getInstance();
        AuthenticationService.getInstance();
        NotificationService.getInstance();

        // will need port information from args, probably

        // hand off to server
        Server server = new Server();
        server.start();
    }
}
