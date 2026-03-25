package ser460.sundevilconnect.server.clubs;

import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.shared.proto.ClubMembershipServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.ClubMembershipServiceProto.*;

public class ClubMembershipController extends ClubMembershipServiceImplBase {

    @Override
    public void requestMembership(RequestMembershipRequest request,
                                  StreamObserver<RequestMembershipResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(RequestMembershipResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void approveMembership(ApproveMembershipRequest request,
                                         StreamObserver<MembershipActionResponse> responseObserver) {
        // TODO: implement
        // TODO: trigger NotificationService to notify student of approval
        responseObserver.onNext(MembershipActionResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void rejectMembership(RejectMembershipRequest request,
                                        StreamObserver<MembershipActionResponse> responseObserver) {
        // TODO: implement
        // TODO: trigger NotificationService to notify student of rejection
        responseObserver.onNext(MembershipActionResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void getPendingRequests(GetPendingRequestsRequest request,
                                   StreamObserver<GetPendingRequestsResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(GetPendingRequestsResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void removeMember(RemoveMemberRequest request,
                             StreamObserver<MembershipActionResponse> responseObserver) {
        // TODO: implement
        // TODO: trigger notification of membership removal
        responseObserver.onNext(MembershipActionResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void getMembershipsForStudent(GetMembershipsForStudentRequest request,
                                         StreamObserver<GetMembershipsForStudentResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(GetMembershipsForStudentResponse.newBuilder().build());
        responseObserver.onCompleted();
    }
}
