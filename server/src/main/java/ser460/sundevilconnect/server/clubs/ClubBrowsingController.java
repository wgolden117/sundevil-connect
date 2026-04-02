package ser460.sundevilconnect.server.clubs;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.shared.proto.ClubBrowsingServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.ClubBrowsingServiceProto.*;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

import java.sql.SQLException;
import java.util.List;

public class ClubBrowsingController extends ClubBrowsingServiceImplBase {
    private final ClubDAO clubDAO;

    public ClubBrowsingController(ClubDAO clubDAO) {
        this.clubDAO = clubDAO;
    }

    @Override
    public void getAllClubs(GetAllClubsRequest request,
                            StreamObserver<ClubListResponse> responseObserver) {
        try {
            List<Club> clubs = clubDAO.getAllClubs();

            ClubListResponse response = ClubListResponse.newBuilder()
                    .addAllClubs(clubs
                            .stream()
                            .map(this::toProto)
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
                            .map(this::toProto)
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
                            .map(this::toProto)
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
                                .setClub(toProto(club))
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
        // TODO: implement
        responseObserver.onError(Status.UNIMPLEMENTED
                .withDescription("getClubMembers not yet implemented")
                .asException());
    }

    private EntitiesProto.Club toProto(Club club) {
        return EntitiesProto.Club.newBuilder()
                .setClubId(club.getClubId())
                .setName(club.getName())
                .setDescription(club.getDescription())
                .setCategory(club.getCategory())
                .setStatus(club.getStatus())
                .setFoundedDate(club.getFoundedDate().toString())
                .build();
    }
}
