package ser460.sundevilconnect.client.test;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import ser460.sundevilconnect.shared.proto.ClubBrowsingServiceGrpc;
import ser460.sundevilconnect.shared.proto.ClubBrowsingServiceProto;


public class ClubBrowsingTest {
    /// Test executable that checks that the Club Browser Controller RPCs are set up correctly
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        ClubBrowsingServiceGrpc.ClubBrowsingServiceBlockingStub stub =
                ClubBrowsingServiceGrpc.newBlockingStub(channel);

        // Test getAllClubs
        ClubBrowsingServiceProto.ClubListResponse allClubs = stub.getAllClubs(ClubBrowsingServiceProto.GetAllClubsRequest.newBuilder().build());
        System.out.println("All clubs: " + allClubs.getClubsList());

        // Test getClubsByCategory
        ClubBrowsingServiceProto.ClubListResponse techClubs = stub.getClubsByCategory(
                ClubBrowsingServiceProto.GetClubsByCategoryRequest.newBuilder().setCategory("Technology").build());
        System.out.println("Tech clubs: " + techClubs.getClubsList());

        // Test searchClubs
        ClubBrowsingServiceProto.ClubListResponse searchResults = stub.searchClubs(
                ClubBrowsingServiceProto.SearchClubsRequest.newBuilder().setKeyword("Coding").build());
        System.out.println("Search results: " + searchResults.getClubsList());

        int clubId = Integer.parseInt(searchResults.getClubsList().getFirst().getClubId());

        // Test getClubDetails
        ClubBrowsingServiceProto.ClubDetailsResponse details = stub.getClubDetails(
                ClubBrowsingServiceProto.GetClubDetailsRequest.newBuilder().setClubId(String.valueOf(clubId)).build());
        System.out.println("Club details: " + details.getClub());

        // Test getClubMembers
        ClubBrowsingServiceProto.ClubMembersResponse members = stub.getClubMembers(
                ClubBrowsingServiceProto.GetClubMembersRequest.newBuilder().setClubId(String.valueOf(clubId)).build());
        System.out.println("Club members: " + members.getMembersList());

        channel.shutdown();
    }
}
