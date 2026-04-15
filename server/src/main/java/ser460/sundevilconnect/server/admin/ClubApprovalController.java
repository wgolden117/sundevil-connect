package ser460.sundevilconnect.server.admin;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.server.clubs.Club;
import ser460.sundevilconnect.server.clubs.ClubDAO;
import ser460.sundevilconnect.shared.proto.ClubApprovalServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.ClubApprovalServiceProto.*;

import java.util.List;

public class ClubApprovalController extends ClubApprovalServiceImplBase {
    private final ClubDAO clubDAO;

    public ClubApprovalController(ClubDAO clubDAO) {
        this.clubDAO = clubDAO;
    }

    @Override
    public void submitClubForApproval(SubmitClubForApprovalRequest request,
                                      StreamObserver<ClubApprovalActionResponse> responseObserver) {
        try {
            Club club = new Club();
            club.setName(request.getNewClub().getName());
            club.setDescription(request.getNewClub().getDescription());
            club.setCategory(request.getNewClub().getCategory());
            clubDAO.insertClub(club);
            responseObserver.onNext(ClubApprovalActionResponse.newBuilder()
                    .setSuccess(true)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException());
        }
    }

    @Override
    public void approveClub(EvaluateClubRequest request,
                            StreamObserver<ClubApprovalActionResponse> responseObserver) {
        try {
            boolean success = clubDAO.approveClub(Integer.parseInt(request.getClubId()));
            responseObserver.onNext(ClubApprovalActionResponse.newBuilder()
                    .setSuccess(success)
                    .build());
            responseObserver.onCompleted();

            // TODO: notify student that club is approved

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException());
        }
    }

    @Override
    public void rejectClub(EvaluateClubRequest request,
                           StreamObserver<ClubApprovalActionResponse> responseObserver) {
        try {
            boolean success = clubDAO.rejectClub(Integer.parseInt(request.getClubId()));
            responseObserver.onNext(ClubApprovalActionResponse.newBuilder()
                    .setSuccess(success)
                    .build());
            responseObserver.onCompleted();

            // TODO: notify student that club is rejected

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException());
        }
    }

    @Override
    public void getPendingClubs(GetPendingClubsRequest request,
                                StreamObserver<GetPendingClubsResponse> responseObserver) {
        try {
            List<Club> clubs = clubDAO.getPendingClubs();
            responseObserver.onNext(GetPendingClubsResponse.newBuilder()
                    .addAllPendingClubs(clubs.stream().map(ClubDAO::toProto).toList())
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asException());
        }
    }
}
