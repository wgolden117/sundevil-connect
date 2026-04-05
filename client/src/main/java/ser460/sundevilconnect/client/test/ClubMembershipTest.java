package ser460.sundevilconnect.client.test;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import ser460.sundevilconnect.shared.proto.*;

public class ClubMembershipTest {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        ClubBrowsingServiceGrpc.ClubBrowsingServiceBlockingStub browsingStub =
                ClubBrowsingServiceGrpc.newBlockingStub(channel);
        ClubMembershipServiceGrpc.ClubMembershipServiceBlockingStub membershipStub =
                ClubMembershipServiceGrpc.newBlockingStub(channel);

        // Get all clubs and find Music Society by name
        ClubBrowsingServiceProto.ClubListResponse allClubs = browsingStub.getAllClubs(ClubBrowsingServiceProto.GetAllClubsRequest.newBuilder().build());
        String musicClubId = allClubs.getClubsList().stream()
                .filter(c -> c.getName().equals("Sun Devil Music Society"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Music Society not found"))
                .getClubId();
        System.out.println("Music Society ID: " + musicClubId);

        // Get Frank's memberships to find his userId
        // We know he's a member of Coding Club, so find him there
        ClubBrowsingServiceProto.ClubMembersResponse codingClubMembers = browsingStub.getClubMembers(
                ClubBrowsingServiceProto.GetClubMembersRequest.newBuilder()
                        .setClubId(allClubs.getClubsList().stream()
                                .filter(c -> c.getName().equals("ASU Coding Club"))
                                .findFirst()
                                .orElseThrow()
                                .getClubId())
                        .build());
        String frankId = codingClubMembers.getMembersList().stream()
                .filter(m -> m.getStudent().getDisplayName().equals("Frank Castle"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Frank Castle not found"))
                .getStudent().getUserId();
        System.out.println("Frank's user ID: " + frankId);

        String peterId = codingClubMembers.getMembersList().stream()
                .filter(m -> m.getStudent().getDisplayName().equals("Peter Parker"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Peter Parker not found"))
                .getStudent().getUserId();
        System.out.println("Peter's user ID: " + peterId);

        // Frank requests to join Music Society
        System.out.println("\n--- Testing requestMembership ---");
        ClubMembershipServiceProto.RequestMembershipResponse requestResponse = membershipStub.requestMembership(
                ClubMembershipServiceProto.RequestMembershipRequest.newBuilder()
                        .setStudentId(frankId)
                        .setClubId(musicClubId)
                        .build());
        System.out.println("Request created: " + requestResponse.getSuccess());
        System.out.println("Request ID: " + requestResponse.getRequestId());
        String requestId = requestResponse.getRequestId();

        // Get pending requests for Music Society
        System.out.println("\n--- Testing getPendingRequests ---");
        ClubMembershipServiceProto.GetPendingRequestsResponse pendingResponse = membershipStub.getPendingRequests(
                ClubMembershipServiceProto.GetPendingRequestsRequest.newBuilder()
                        .setClubId(musicClubId)
                        .build());
        System.out.println("Pending requests: " + pendingResponse.getRequestsList());

        // Peter approves the request
        System.out.println("\n--- Testing approveMembership ---");
        ClubMembershipServiceProto.MembershipActionResponse approveResponse = membershipStub.approveMembership(
                ClubMembershipServiceProto.ApproveMembershipRequest.newBuilder()
                        .setRequestId(requestId)
                        .setApproverId(peterId)
                        .build());
        System.out.println("Approved: " + approveResponse.getSuccess());

        // Verify Frank now has the membership
        System.out.println("\n--- Testing getMembershipsForStudent ---");
        ClubMembershipServiceProto.GetMembershipsForStudentResponse membershipsResponse = membershipStub.getMembershipsForStudent(
                ClubMembershipServiceProto.GetMembershipsForStudentRequest.newBuilder()
                        .setUserId(frankId)
                        .build());
        System.out.println("Frank's memberships: " + membershipsResponse.getMembershipsList());

        channel.shutdown();
    }
}
