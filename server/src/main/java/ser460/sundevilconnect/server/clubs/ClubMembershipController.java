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

                                int studentId = Integer.parseInt(membershipRequest.getStudent().getUserId());
                                int clubId = Integer.parseInt(membershipRequest.getClub().getClubId());
                                String clubName = membershipRequest.getClub().getName();

                                clubMembershipDAO.createMembership(studentId, clubId, false);

                                // send notification
                                var notification = ser460.sundevilconnect.shared.proto.NotificationServiceProto.NotificationMessage.newBuilder()
                                        .setNotificationId(java.util.UUID.randomUUID().toString())
                                        .setUserId(String.valueOf(studentId))
                                        .setMessage("You have been approved to join " + clubName)
                                        .setType(ser460.sundevilconnect.shared.proto.NotificationServiceProto.NotificationType.MEMBERSHIP_APPROVED)
                                        .setTimestamp(java.time.Instant.now().toString())
                                        .setIsRead(false)
                                        .build();

                                ser460.sundevilconnect.server.core.NotificationService.getInstance()
                                        .notifyObservers(java.util.List.of(String.valueOf(studentId)), notification);
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
            int requestId = Integer.parseInt(request.getRequestId());
            int approverId = Integer.parseInt(request.getApproverId());

            boolean updated = membershipRequestDAO.updateRequestStatus(
                    requestId,
                    "REJECTED",
                    approverId
            );

            // send notification
            if (updated) {
                membershipRequestDAO.getRequestById(requestId).ifPresent(req -> {

                    String studentId = req.getStudent().getUserId();
                    String clubName = req.getClub().getName();

                    var notification = ser460.sundevilconnect.shared.proto.NotificationServiceProto.NotificationMessage.newBuilder()
                            .setNotificationId(java.util.UUID.randomUUID().toString())
                            .setUserId(studentId)
                            .setMessage("Your request to join " + clubName + " was declined")
                            .setType(ser460.sundevilconnect.shared.proto.NotificationServiceProto.NotificationType.MEMBERSHIP_REJECTED)
                            .setTimestamp(java.time.Instant.now().toString())
                            .setIsRead(false)
                            .build();

                    ser460.sundevilconnect.server.core.NotificationService.getInstance()
                            .notifyObservers(java.util.List.of(studentId), notification);
                });
            }

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
            int membershipId = Integer.parseInt(request.getMemberId());

            // Get membership before removing
            var membership = clubMembershipDAO.getMembershipById(membershipId);

            boolean removed = clubMembershipDAO.removeMember(membershipId);

            // Send notification
            if (removed && membership != null) {

                String studentId = membership.getStudent().getUserId();
                String clubName = membership.getClub().getName();

                var notification = ser460.sundevilconnect.shared.proto.NotificationServiceProto.NotificationMessage.newBuilder()
                        .setNotificationId(java.util.UUID.randomUUID().toString())
                        .setUserId(studentId)
                        .setMessage("You have been removed from " + clubName)
                        .setType(ser460.sundevilconnect.shared.proto.NotificationServiceProto.NotificationType.MEMBERSHIP_REJECTED)
                        .setTimestamp(java.time.Instant.now().toString())
                        .setIsRead(false)
                        .build();

                ser460.sundevilconnect.server.core.NotificationService.getInstance()
                        .notifyObservers(java.util.List.of(studentId), notification);
            }

            responseObserver.onNext(
                    MembershipActionResponse.newBuilder()
                            .setSuccess(removed)
                            .build()
            );
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

    @Override
    public void getClubMembershipStatus(GetClubMembershipStatusRequest request,
                                        StreamObserver<GetClubMembershipStatusResponse> responseObserver) {
        try {
            int studentId = Integer.parseInt(request.getUserId());
            int clubId = Integer.parseInt(request.getClubId());
            String status = clubMembershipDAO.getMembershipStatus(studentId, clubId);

            if (status == null) {
                status = membershipRequestDAO.getRequestStatus(studentId, clubId);
            }

            if (status == null) {
                status = "NONE";
            }

            responseObserver.onNext(GetClubMembershipStatusResponse.newBuilder()
                    .setStatus(status)
                    .build());
            responseObserver.onCompleted();
        } catch (SQLException e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException());
        }
    }

    @Override
    public void getRequestsForStudent(GetMembershipsForStudentRequest request,
                                      StreamObserver<GetRequestsForStudentResponse> responseObserver) {
        try {
            GetRequestsForStudentResponse response = GetRequestsForStudentResponse.newBuilder()
                    .addAllRequests(membershipRequestDAO.getRequestsForStudent(
                                    Integer.parseInt(request.getUserId()))
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
    public void promoteMember(PromoteMemberRequest request,
                              StreamObserver<MembershipActionResponse> responseObserver) {
        try {
            boolean promoted = clubMembershipDAO.promoteMember(
                    Integer.parseInt(request.getMembershipId())
            );
            responseObserver.onNext(MembershipActionResponse.newBuilder()
                    .setSuccess(promoted)
                    .build());
            responseObserver.onCompleted();
        } catch (SQLException e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException());
        }
    }
}
