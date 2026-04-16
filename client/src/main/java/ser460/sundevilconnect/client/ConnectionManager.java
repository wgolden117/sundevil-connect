package ser460.sundevilconnect.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import ser460.sundevilconnect.shared.proto.*;

public class ConnectionManager {
    private static final ConnectionManager instance = new ConnectionManager();

    private ManagedChannel channel;

    private AuthServiceGrpc.AuthServiceBlockingStub authStub;
    private ClubBrowsingServiceGrpc.ClubBrowsingServiceBlockingStub clubBrowsingStub;
    private ClubMembershipServiceGrpc.ClubMembershipServiceBlockingStub clubMembershipStub;
    private EventBrowsingServiceGrpc.EventBrowsingServiceBlockingStub eventBrowsingStub;
    private EventManagementServiceGrpc.EventManagementServiceBlockingStub eventManagementStub;
    private EventRegistrationServiceGrpc.EventRegistrationServiceBlockingStub eventRegistrationStub;
    private AnnouncementServiceGrpc.AnnouncementServiceBlockingStub announcementStub;
    private ClubApprovalServiceGrpc.ClubApprovalServiceBlockingStub clubApprovalStub;
    private ContentModerationServiceGrpc.ContentModerationServiceBlockingStub contentModerationStub;
    private NotificationServiceGrpc.NotificationServiceStub notificationStub;
    private NotificationServiceGrpc.NotificationServiceBlockingStub notificationBlockingStub;

    private ConnectionManager() {}

    public static ConnectionManager getInstance() {
        return instance;
    }

    public void connect(String host, int port) {
        channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();

        authStub = AuthServiceGrpc.newBlockingStub(channel);
        clubBrowsingStub = ClubBrowsingServiceGrpc.newBlockingStub(channel);
        clubMembershipStub = ClubMembershipServiceGrpc.newBlockingStub(channel);
        eventBrowsingStub = EventBrowsingServiceGrpc.newBlockingStub(channel);
        eventManagementStub = EventManagementServiceGrpc.newBlockingStub(channel);
        eventRegistrationStub = EventRegistrationServiceGrpc.newBlockingStub(channel);
        announcementStub = AnnouncementServiceGrpc.newBlockingStub(channel);
        clubApprovalStub = ClubApprovalServiceGrpc.newBlockingStub(channel);
        contentModerationStub = ContentModerationServiceGrpc.newBlockingStub(channel);
        notificationStub = NotificationServiceGrpc.newStub(channel);
        notificationBlockingStub = NotificationServiceGrpc.newBlockingStub(channel);
    }

    public AuthServiceGrpc.AuthServiceBlockingStub getAuthStub() { return authStub; }
    public ClubBrowsingServiceGrpc.ClubBrowsingServiceBlockingStub getClubBrowsingStub() { return clubBrowsingStub; }
    public ClubMembershipServiceGrpc.ClubMembershipServiceBlockingStub getClubMembershipStub() { return clubMembershipStub; }
    public EventBrowsingServiceGrpc.EventBrowsingServiceBlockingStub getEventBrowsingStub() { return eventBrowsingStub; }
    public EventManagementServiceGrpc.EventManagementServiceBlockingStub getEventManagementStub() { return eventManagementStub; }
    public EventRegistrationServiceGrpc.EventRegistrationServiceBlockingStub getEventRegistrationStub() { return eventRegistrationStub; }
    public AnnouncementServiceGrpc.AnnouncementServiceBlockingStub getAnnouncementStub() { return announcementStub; }
    public ClubApprovalServiceGrpc.ClubApprovalServiceBlockingStub getClubApprovalStub() { return clubApprovalStub; }
    public ContentModerationServiceGrpc.ContentModerationServiceBlockingStub getContentModerationStub() { return contentModerationStub; }
    public NotificationServiceGrpc.NotificationServiceStub getNotificationStub() { return notificationStub; }
    public NotificationServiceGrpc.NotificationServiceBlockingStub getNotificationBlockingStub() {
        return notificationBlockingStub;
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown();
    }
}