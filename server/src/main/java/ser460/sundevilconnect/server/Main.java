package ser460.sundevilconnect.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import ser460.sundevilconnect.server.admin.*;
import ser460.sundevilconnect.server.announcements.AnnouncementController;
import ser460.sundevilconnect.server.announcements.AnnouncementDAO;
import ser460.sundevilconnect.server.auth.AuthenticationController;
import ser460.sundevilconnect.server.clubs.*;
import ser460.sundevilconnect.server.core.*;
import ser460.sundevilconnect.server.events.*;

public class Main {
    public static void main(String[] args) throws Exception {
        // create and init singleton services
        DatabaseService db = DatabaseService.getInstance();
        db.initializeDatabase();
        db.insertTestData();

        AuthenticationService.getInstance();
        NotificationService.getInstance();

        // get port/host information from args

        // build entity Data Access Objects
        ClubDAO clubDAO = new ClubDAO(db);
        ClubMembershipDAO clubMembershipDAO = new ClubMembershipDAO(db);
        MembershipRequestDAO membershipRequestDAO = new MembershipRequestDAO(db);
        AnnouncementDAO announcementDAO = new AnnouncementDAO(db);

        // build and start gRPC server
        Server server = ServerBuilder
                .forPort(8080)
                .addService(NotificationService.getInstance()) // add for each service implementation
                .addService(new ClubApprovalController())
                .addService(new ContentModerationController())
                .addService(new AnnouncementController(announcementDAO, clubMembershipDAO))
                .addService(new AuthenticationController())
                .addService(new ClubBrowsingController(clubDAO, clubMembershipDAO))
                .addService(new ClubMembershipController(clubMembershipDAO, membershipRequestDAO))
                .addService(new EventBrowsingController())
                .addService(new EventManagementController())
                .addService(new EventRegistrationController())
                .build()
                .start();
        System.out.println("SERVER_READY");

        server.awaitTermination();
    }
}
