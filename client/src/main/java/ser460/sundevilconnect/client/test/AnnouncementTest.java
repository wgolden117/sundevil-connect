package ser460.sundevilconnect.client.test;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import ser460.sundevilconnect.shared.proto.*;

public class AnnouncementTest {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        ClubBrowsingServiceGrpc.ClubBrowsingServiceBlockingStub browsingStub =
                ClubBrowsingServiceGrpc.newBlockingStub(channel);
        AnnouncementServiceGrpc.AnnouncementServiceBlockingStub announcementStub =
                AnnouncementServiceGrpc.newBlockingStub(channel);

        // Bootstrap IDs from browsing stub
        ClubBrowsingServiceProto.ClubListResponse allClubs = browsingStub.getAllClubs(
                ClubBrowsingServiceProto.GetAllClubsRequest.newBuilder().build());

        String codingClubId = allClubs.getClubsList().stream()
                .filter(c -> c.getName().equals("ASU Coding Club"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("ASU Coding Club not found"))
                .getClubId();
        System.out.println("Coding Club ID: " + codingClubId);

        ClubBrowsingServiceProto.ClubMembersResponse members = browsingStub.getClubMembers(
                ClubBrowsingServiceProto.GetClubMembersRequest.newBuilder()
                        .setClubId(codingClubId)
                        .build());

        String frankId = members.getMembersList().stream()
                .filter(m -> m.getStudent().getDisplayName().equals("Frank Castle"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Frank Castle not found"))
                .getStudent().getUserId();
        System.out.println("Frank's user ID: " + frankId);

        String peterId = members.getMembersList().stream()
                .filter(m -> m.getStudent().getDisplayName().equals("Peter Parker"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Peter Parker not found"))
                .getStudent().getUserId();
        System.out.println("Peter's user ID: " + peterId);

        // 1. Get announcements as Frank (member) - should only see published
        System.out.println("\n--- Get announcements as Frank (member) ---");
        AnnouncementServiceProto.GetAnnouncementsResponse frankAnnouncements = announcementStub.getAnnouncementsForClub(
                AnnouncementServiceProto.GetAnnouncementsForClubRequest.newBuilder()
                        .setClubId(codingClubId)
                        .setRequestingUserId(frankId)
                        .build());
        System.out.println("Frank sees: " + frankAnnouncements.getAnnouncementsList());

        // 2. Get announcements as Peter (leader) - should see drafts too
        System.out.println("\n--- Get announcements as Peter (leader) ---");
        AnnouncementServiceProto.GetAnnouncementsResponse peterAnnouncements = announcementStub.getAnnouncementsForClub(
                AnnouncementServiceProto.GetAnnouncementsForClubRequest.newBuilder()
                        .setClubId(codingClubId)
                        .setRequestingUserId(peterId)
                        .build());
        System.out.println("Peter sees: " + peterAnnouncements.getAnnouncementsList());

        // 3. Peter creates a new draft announcement
        System.out.println("\n--- Create announcement ---");
        AnnouncementServiceProto.AnnouncementActionResponse createResponse = announcementStub.createAnnouncement(
                AnnouncementServiceProto.CreateAnnouncementRequest.newBuilder()
                        .setAnnouncement(EntitiesProto.Announcement.newBuilder()
                                .setTitle("Upcoming Study Session")
                                .setBody("We will be hosting a study session next Friday in the engineering building.")
                                .setPostedTo(EntitiesProto.Club.newBuilder().setClubId(codingClubId).build())
                                .setCreatedBy(EntitiesProto.UserSummary.newBuilder().setUserId(peterId).build())
                                .setStatus("DRAFT")
                                .build())
                        .build());
        System.out.println("Created: " + createResponse.getSuccess());

        // Verify draft appears for Peter
        peterAnnouncements = announcementStub.getAnnouncementsForClub(
                AnnouncementServiceProto.GetAnnouncementsForClubRequest.newBuilder()
                        .setClubId(codingClubId)
                        .setRequestingUserId(peterId)
                        .build());
        System.out.println("Peter sees after create: " + peterAnnouncements.getAnnouncementsList());

        String newAnnouncementId = peterAnnouncements.getAnnouncementsList().stream()
                .filter(a -> a.getTitle().equals("Upcoming Study Session"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("New announcement not found"))
                .getAnnouncementId();
        System.out.println("New announcement ID: " + newAnnouncementId);

        // 4. Peter edits the announcement
        System.out.println("\n--- Edit announcement ---");
        AnnouncementServiceProto.AnnouncementActionResponse editResponse = announcementStub.editAnnouncement(
                AnnouncementServiceProto.EditAnnouncementRequest.newBuilder()
                        .setEditedAnnouncement(EntitiesProto.Announcement.newBuilder()
                                .setAnnouncementId(newAnnouncementId)
                                .setTitle("Upcoming Study Session - Updated")
                                .setBody("We will be hosting a study session next Friday at 5pm in BYENG 210.")
                                .setStatus("DRAFT")
                                .build())
                        .build());
        System.out.println("Edited: " + editResponse.getSuccess());

        // Verify edit
        peterAnnouncements = announcementStub.getAnnouncementsForClub(
                AnnouncementServiceProto.GetAnnouncementsForClubRequest.newBuilder()
                        .setClubId(codingClubId)
                        .setRequestingUserId(peterId)
                        .build());
        System.out.println("Peter sees after edit: " + peterAnnouncements.getAnnouncementsList());

        // 5. Peter publishes the announcement
        System.out.println("\n--- Publish announcement ---");
        AnnouncementServiceProto.AnnouncementActionResponse publishResponse = announcementStub.publishAnnouncement(
                AnnouncementServiceProto.PublishAnnouncementRequest.newBuilder()
                        .setAnnouncementId(newAnnouncementId)
                        .setIsPublished(true)
                        .build());
        System.out.println("Published: " + publishResponse.getSuccess());

        // Verify Frank can now see it
        frankAnnouncements = announcementStub.getAnnouncementsForClub(
                AnnouncementServiceProto.GetAnnouncementsForClubRequest.newBuilder()
                        .setClubId(codingClubId)
                        .setRequestingUserId(frankId)
                        .build());
        System.out.println("Frank sees after publish: " + frankAnnouncements.getAnnouncementsList());

        // 6. Peter deletes the announcement
        System.out.println("\n--- Delete announcement ---");
        AnnouncementServiceProto.AnnouncementActionResponse deleteResponse = announcementStub.deleteAnnouncement(
                AnnouncementServiceProto.DeleteAnnouncementRequest.newBuilder()
                        .setAnnouncementId(newAnnouncementId)
                        .build());
        System.out.println("Deleted: " + deleteResponse.getSuccess());

        // Verify it's gone for Peter
        peterAnnouncements = announcementStub.getAnnouncementsForClub(
                AnnouncementServiceProto.GetAnnouncementsForClubRequest.newBuilder()
                        .setClubId(codingClubId)
                        .setRequestingUserId(peterId)
                        .build());
        System.out.println("Peter sees after delete: " + peterAnnouncements.getAnnouncementsList());

        channel.shutdown();
    }
}
