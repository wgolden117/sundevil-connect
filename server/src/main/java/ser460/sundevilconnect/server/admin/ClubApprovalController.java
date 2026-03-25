package ser460.sundevilconnect.server.admin;

import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.server.clubs.Club;
import ser460.sundevilconnect.shared.proto.ClubApprovalServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.ClubApprovalServiceProto.*;

import java.util.List;

public class ClubApprovalController extends ClubApprovalServiceImplBase {
    private List<Club> pendingClubs;

    @Override
    public void submitClubForApproval(SubmitClubForApprovalRequest request,
                                      StreamObserver<ClubApprovalActionResponse> responseObserver) {
        // TODO implement
        responseObserver.onNext(ClubApprovalActionResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void approveClub(EvaluateClubRequest request,
                            StreamObserver<ClubApprovalActionResponse> responseObserver) {
        // TODO: implement
        // TODO: notify student that club is approved
        responseObserver.onNext(ClubApprovalActionResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void rejectClub(EvaluateClubRequest request,
                           StreamObserver<ClubApprovalActionResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(ClubApprovalActionResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void getPendingClubs(GetPendingClubsRequest request,
                                StreamObserver<GetPendingClubsResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(GetPendingClubsResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    // this one doesn't seem to make a lot of sense anymore
    public Club reviewClubDetails() { return null; }
}
