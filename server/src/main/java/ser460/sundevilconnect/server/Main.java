package ser460.sundevilconnect.server;

import ser460.sundevilconnect.server.admin.*;
import ser460.sundevilconnect.server.announcements.AnnouncementController;
import ser460.sundevilconnect.server.auth.AuthenticationController;
import ser460.sundevilconnect.server.clubs.*;
import ser460.sundevilconnect.server.core.*;
import ser460.sundevilconnect.server.events.*;

public class Main {
    public static void main(String[] args) throws Exception {
        // create and init singleton services
        DatabaseService db = DatabaseService.getInstance();
        db.initializeDatabase();
        db.insertTestUser();
        AuthenticationService.getInstance();
        NotificationService.getInstance();

        // get port/host information from args

        // build and start gRPC server
        io.grpc.Server server = io.grpc.ServerBuilder
                .forPort(8080)
                .addService(NotificationService.getInstance()) // add for each service implementation
                .addService(new ClubApprovalController())
                .addService(new ContentModerationController())
                .addService(new AnnouncementController())
                .addService(new AuthenticationController())
                .addService(new ClubBrowsingController())
                .addService(new ClubMembershipController())
                .addService(new EventBrowsingController())
                .addService(new EventManagementController())
                .addService(new EventRegistrationController())
                .build()
                .start();
        System.out.println("SERVER_READY");

        server.awaitTermination();
    }
}
