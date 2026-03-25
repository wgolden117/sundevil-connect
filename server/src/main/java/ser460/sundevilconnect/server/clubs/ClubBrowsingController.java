package ser460.sundevilconnect.server.clubs;

import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.shared.proto.ClubBrowsingServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.ClubBrowsingServiceProto.*;

public class ClubBrowsingController extends ClubBrowsingServiceImplBase {

    @Override
    public void getAllClubs(GetAllClubsRequest request,
                            StreamObserver<ClubListResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(ClubListResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void getClubsByCategory(GetClubsByCategoryRequest request,
                                   StreamObserver<ClubListResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(ClubListResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void searchClubs(SearchClubsRequest request,
                            StreamObserver<ClubListResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(ClubListResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void getClubDetails(GetClubDetailsRequest request,
                               StreamObserver<ClubDetailsResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(ClubDetailsResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void getClubMembers(GetClubMembersRequest request,
                               StreamObserver<ClubMembersResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(ClubMembersResponse.newBuilder().build());
        responseObserver.onCompleted();
    }
}
