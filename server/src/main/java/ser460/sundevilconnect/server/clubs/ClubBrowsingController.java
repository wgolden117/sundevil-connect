package ser460.sundevilconnect.server.clubs;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.shared.proto.ClubBrowsingServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.ClubBrowsingServiceProto.*;

import java.sql.SQLException;
import java.util.List;

public class ClubBrowsingController extends ClubBrowsingServiceImplBase {
    private final ClubDAO clubDAO;
    private final ClubMembershipDAO clubMembershipDAO;

    public ClubBrowsingController(ClubDAO clubDAO, ClubMembershipDAO clubMembershipDAO) {
        this.clubDAO = clubDAO;
        this.clubMembershipDAO = clubMembershipDAO;
    }

    @Override
    public void getAllClubs(GetAllClubsRequest request,
                            StreamObserver<ClubListResponse> responseObserver) {
        try {
            List<Club> clubs = clubDAO.getAllClubs();

            ClubListResponse response = ClubListResponse.newBuilder()
                    .addAllClubs(clubs
                            .stream()
                            .map(ClubDAO::toProto)
                            .toList())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (SQLException e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException()
            );
        }
    }

    @Override
    public void getClubsByCategory(GetClubsByCategoryRequest request,
                                   StreamObserver<ClubListResponse> responseObserver) {
        try {
            ClubListResponse response = ClubListResponse.newBuilder()
                    .addAllClubs(clubDAO.getClubsByCategory(request.getCategory())
                            .stream()
                            .map(ClubDAO::toProto)
                            .toList())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (SQLException e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException()
            );
        }
    }

    @Override
    public void searchClubs(SearchClubsRequest request,
                            StreamObserver<ClubListResponse> responseObserver) {
        try {
            ClubListResponse response = ClubListResponse.newBuilder()
                    .addAllClubs(clubDAO.searchClubs(request.getKeyword())
                            .stream()
                            .map(ClubDAO::toProto)
                            .toList())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (SQLException e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException()
            );
        }
    }

    @Override
    public void getClubDetails(GetClubDetailsRequest request,
                               StreamObserver<ClubDetailsResponse> responseObserver) {
        try {
            clubDAO.getClubById(Integer.parseInt(request.getClubId())).ifPresentOrElse(
                    club -> {
                        responseObserver.onNext(ClubDetailsResponse.newBuilder()
                                .setClub(ClubDAO.toProto(club))
                                .build());
                        responseObserver.onCompleted();
                    },
                    () -> responseObserver.onError(Status.NOT_FOUND
                            .withDescription("Club not found: " + request.getClubId())
                            .asException())
            );
        } catch (SQLException e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException()
            );
        }
    }

    @Override
    public void getClubMembers(GetClubMembersRequest request,
                               StreamObserver<ClubMembersResponse> responseObserver) {
        try {
            List<ClubMembership> members = clubMembershipDAO.getMembersForClub(
                    Integer.parseInt(request.getClubId()));
            ClubMembersResponse response = ClubMembersResponse.newBuilder()
                    .addAllMembers(members.stream().map(ClubMembershipDAO::toProto).toList())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (SQLException e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException()
            );
        }
    }
}
