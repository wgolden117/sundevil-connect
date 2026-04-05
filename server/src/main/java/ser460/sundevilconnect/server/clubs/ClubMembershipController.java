package ser460.sundevilconnect.server.clubs;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.shared.proto.ClubMembershipServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.ClubMembershipServiceProto.*;

import java.sql.SQLException;

public class ClubMembershipController extends ClubMembershipServiceImplBase {
    private final ClubMembershipDAO clubMembershipDAO;
    private final MembershipRequestDAO membershipRequestDAO;

    public ClubMembershipController(ClubMembershipDAO clubMembershipDAO,
                                    MembershipRequestDAO membershipRequestDAO) {
        this.clubMembershipDAO = clubMembershipDAO;
        this.membershipRequestDAO = membershipRequestDAO;
    }

    @Override
    public void requestMembership(RequestMembershipRequest request,
                                  StreamObserver<RequestMembershipResponse> responseObserver) {
        try {
            String requestId = membershipRequestDAO.createRequest(
                    Integer.parseInt(request.getStudentId()),
                    Integer.parseInt(request.getClubId())
            );
            responseObserver.onNext(RequestMembershipResponse.newBuilder()
                    .setSuccess(true)
                    .setRequestId(requestId)
                    .build());
            responseObserver.onCompleted();
        } catch (SQLException e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException());
        }
    }

    @Override
    public void approveMembership(ApproveMembershipRequest request,
                                  StreamObserver<MembershipActionResponse> responseObserver) {
        try {
            int requestId = Integer.parseInt(request.getRequestId());
            int approverId = Integer.parseInt(request.getApproverId());

            membershipRequestDAO.getRequestById(requestId).ifPresentOrElse(
                    membershipRequest -> {
                        try {
                            boolean updated = membershipRequestDAO.updateRequestStatus(
                                    requestId, "APPROVED", approverId
                            );
                            if (updated) {
                                clubMembershipDAO.createMembership(
                                        Integer.parseInt(membershipRequest.getStudent().getUserId()),
                                        Integer.parseInt(membershipRequest.getClub().getClubId())
                                );
                            }
                            responseObserver.onNext(MembershipActionResponse.newBuilder()
                                    .setSuccess(updated)
                                    .build());
                            responseObserver.onCompleted();
                        } catch (SQLException e) {
                            responseObserver.onError(Status.INTERNAL
                                    .withDescription(e.getMessage()).asException());
                        }
                    },
                    () -> responseObserver.onError(Status.NOT_FOUND
                            .withDescription("Request not found: " + requestId)
                            .asException())
            );
        } catch (SQLException e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException());
        }
    }

    @Override
    public void rejectMembership(RejectMembershipRequest request,
                                 StreamObserver<MembershipActionResponse> responseObserver) {
        try {
            boolean updated = membershipRequestDAO.updateRequestStatus(
                    Integer.parseInt(request.getRequestId()),
                    "REJECTED",
                    Integer.parseInt(request.getApproverId())
            );
            responseObserver.onNext(MembershipActionResponse.newBuilder()
                    .setSuccess(updated)
                    .build());
            responseObserver.onCompleted();
        } catch (SQLException e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException());
        }
    }

    @Override
    public void getPendingRequests(GetPendingRequestsRequest request,
                                   StreamObserver<GetPendingRequestsResponse> responseObserver) {
        try {
            GetPendingRequestsResponse response = GetPendingRequestsResponse.newBuilder()
                    .addAllRequests(membershipRequestDAO.getPendingRequestsForClub(
                            Integer.parseInt(request.getClubId()))
                            .stream()
                            .map(MembershipRequestDAO::toProto)
                            .toList())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (SQLException e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException());
        }
    }

    @Override
    public void removeMember(RemoveMemberRequest request,
                             StreamObserver<MembershipActionResponse> responseObserver) {
        try {
            boolean removed = clubMembershipDAO.removeMember(
                    Integer.parseInt(request.getMemberId())
            );
            responseObserver.onNext(MembershipActionResponse.newBuilder()
                    .setSuccess(removed)
                    .build());
            responseObserver.onCompleted();
        } catch (SQLException e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException());
        }
    }

    @Override
    public void getMembershipsForStudent(GetMembershipsForStudentRequest request,
                                         StreamObserver<GetMembershipsForStudentResponse> responseObserver) {
        try {
            GetMembershipsForStudentResponse response = GetMembershipsForStudentResponse.newBuilder()
                    .addAllMemberships(clubMembershipDAO.getMembershipForStudent(
                            Integer.parseInt(request.getUserId()))
                            .stream()
                            .map(ClubMembershipDAO::toProto)
                            .toList())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (SQLException e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException());
        }
    }
}
