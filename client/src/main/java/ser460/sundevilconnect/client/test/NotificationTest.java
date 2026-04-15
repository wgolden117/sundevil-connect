package ser460.sundevilconnect.client.test;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.shared.proto.*;

import java.util.*;

public class NotificationTest {

    // CHANGED: Track per-user notifications
    private static final Map<String, List<NotificationServiceProto.NotificationMessage>> userNotifications = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        var notificationStub = NotificationServiceGrpc.newStub(channel);
        var eventRegStub = EventRegistrationServiceGrpc.newBlockingStub(channel);
        var eventMgmtStub = EventManagementServiceGrpc.newBlockingStub(channel);
        var membershipStub = ClubMembershipServiceGrpc.newBlockingStub(channel);
        var announcementStub = AnnouncementServiceGrpc.newBlockingStub(channel);
        var browsingStub = ClubBrowsingServiceGrpc.newBlockingStub(channel);

        // --------------------------------------------
        // Get club + users
        // --------------------------------------------
        var clubs = browsingStub.getAllClubs(
                ClubBrowsingServiceProto.GetAllClubsRequest.newBuilder().build()
        );

        var codingClub = clubs.getClubsList().stream()
                .filter(c -> c.getName().equals("ASU Coding Club"))
                .findFirst()
                .orElseThrow();

        String clubId = codingClub.getClubId();

        var members = browsingStub.getClubMembers(
                ClubBrowsingServiceProto.GetClubMembersRequest.newBuilder()
                        .setClubId(clubId)
                        .build()
        );

        String studentId = members.getMembersList().stream()
                .filter(m -> m.getStudent().getDisplayName().equals("Frank Castle"))
                .findFirst()
                .orElseThrow()
                .getStudent().getUserId();

        String leaderId = members.getMembersList().stream()
                .filter(m -> m.getStudent().getDisplayName().equals("Peter Parker"))
                .findFirst()
                .orElseThrow()
                .getStudent().getUserId();

        String maryId = members.getMembersList().stream()
                .filter(m -> m.getStudent().getDisplayName().equals("Mary Jane"))
                .findFirst()
                .orElseThrow()
                .getStudent().getUserId();

        // NEW: list of subscribers
        List<String> subscribers = List.of(studentId, leaderId, maryId);

        // Initialize tracking map
        subscribers.forEach(userId ->
                userNotifications.put(userId, new ArrayList<>())
        );

        // --------------------------------------------
        // Get event
        // --------------------------------------------
        var eventsResponse = eventMgmtStub.getEventsForClub(
                EventManagementServiceProto.GetEventsForClubRequest.newBuilder()
                        .setClubId(clubId)
                        .build()
        );

        if (eventsResponse.getEventsList().isEmpty()) {
            System.out.println("❌ No events found — cannot continue test");
            return;
        }

        var event = eventsResponse.getEventsList().get(0);
        String eventId = event.getEventId();

        // --------------------------------------------
        // Subscribe (MULTI USER)
        // --------------------------------------------
        System.out.println("Subscribing to notifications...");

        subscribers.forEach(userId -> {
            notificationStub.subscribe(
                    NotificationServiceProto.SubscribeRequest.newBuilder()
                            .setUserId(userId)
                            .build(),
                    new StreamObserver<>() {

                        @Override
                        public void onNext(NotificationServiceProto.NotificationMessage notification) {

                            System.out.println("📩 [" + userId + "] "
                                    + notification.getType() + ": "
                                    + notification.getMessage());

                            userNotifications.get(userId).add(notification);

                            // sanity check
                            if (!notification.getUserId().equals(userId)) {
                                return;
                            }
                        }

                        @Override public void onError(Throwable t) { t.printStackTrace(); }
                        @Override public void onCompleted() {}
                    }
            );
        });

        Thread.sleep(1000);

        // --------------------------------------------
        // TEST 1: Register (Frank only)
        // --------------------------------------------
        System.out.println("\n--- TEST: Event Registration ---");

        eventRegStub.registerStudentForEvent(
                EventRegistrationServiceProto.RegisterStudentForEventRequest.newBuilder()
                        .setStudentId(studentId)
                        .setEventId(eventId)
                        .build()
        );

        Thread.sleep(1000);

        // --------------------------------------------
        // TEST 2: Event Update
        // --------------------------------------------
        System.out.println("\n--- TEST: Event Update ---");

        var updatedEvent = event.toBuilder()
                .setTitle(event.getTitle() + " (Updated)")
                .build();

        eventMgmtStub.updateEvent(
                EventManagementServiceProto.UpdateEventRequest.newBuilder()
                        .setUpdatedEvent(updatedEvent)
                        .build()
        );

        Thread.sleep(1000);

        // --------------------------------------------
        // TEST 3: Cancel (Frank only)
        // --------------------------------------------
        System.out.println("\n--- TEST: Cancel Registration ---");

        var regs = eventRegStub.getRegistrationsForStudent(
                EventRegistrationServiceProto.GetRegistrationsForStudentRequest.newBuilder()
                        .setStudentId(studentId)
                        .build()
        );

        if (!regs.getRegistrationsList().isEmpty()) {
            String regId = regs.getRegistrationsList().get(0).getRegistrationId();

            eventRegStub.cancelRegistration(
                    EventRegistrationServiceProto.CancelRegistrationRequest.newBuilder()
                            .setRegistrationId(regId)
                            .build()
            );
        }

        Thread.sleep(1000);

        // --------------------------------------------
        // TEST 4: Membership Approval (Frank only)
        // --------------------------------------------
        System.out.println("\n--- TEST: Membership Approval ---");

        var roboticsClub = clubs.getClubsList().stream()
                .filter(c -> c.getName().equals("ASU Robotics"))
                .findFirst()
                .orElseThrow();

        String roboticsClubId = roboticsClub.getClubId();

        var requestResponse = membershipStub.requestMembership(
                ClubMembershipServiceProto.RequestMembershipRequest.newBuilder()
                        .setStudentId(studentId)
                        .setClubId(roboticsClubId)
                        .build()
        );

        String requestId = requestResponse.getRequestId();
        System.out.println("Created requestId: " + requestId);

        membershipStub.approveMembership(
                ClubMembershipServiceProto.ApproveMembershipRequest.newBuilder()
                        .setRequestId(requestId)
                        .setApproverId(leaderId)
                        .build()
        );

        Thread.sleep(1000);

        // --------------------------------------------
        // TEST 5: Announcement (ALL members)
        // --------------------------------------------
        System.out.println("\n--- TEST: Announcement Publish ---");

        var announcements = announcementStub.getAnnouncementsForClub(
                AnnouncementServiceProto.GetAnnouncementsForClubRequest.newBuilder()
                        .setClubId(clubId)
                        .setRequestingUserId(leaderId)
                        .build()
        );

        if (!announcements.getAnnouncementsList().isEmpty()) {

            String announcementId = announcements.getAnnouncementsList().get(0).getAnnouncementId();

            announcementStub.publishAnnouncement(
                    AnnouncementServiceProto.PublishAnnouncementRequest.newBuilder()
                            .setAnnouncementId(announcementId)
                            .setIsPublished(true)
                            .build()
            );
        } else {
            System.out.println("⚠ No announcements found");
        }

        Thread.sleep(2000);

        // --------------------------------------------
        // RESULTS (PER USER)
        // --------------------------------------------
        System.out.println("\n========= PER USER RESULTS =========");

        userNotifications.forEach((userId, notifications) -> {
            System.out.println("\nUser: " + userId);
            System.out.println("Total: " + notifications.size());

            notifications.forEach(n ->
                    System.out.println("✔ " + n.getType() + " -> " + n.getMessage())
            );
        });

        channel.shutdown();
    }
}