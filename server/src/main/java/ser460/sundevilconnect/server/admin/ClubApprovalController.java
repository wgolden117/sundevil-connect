package ser460.sundevilconnect.server.admin;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.server.clubs.Club;
import ser460.sundevilconnect.server.clubs.ClubDAO;
import ser460.sundevilconnect.server.clubs.ClubMembershipDAO;
import ser460.sundevilconnect.server.core.NotificationService;
import ser460.sundevilconnect.shared.proto.ClubApprovalServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.ClubApprovalServiceProto.*;
import ser460.sundevilconnect.shared.proto.NotificationServiceProto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ClubApprovalController extends ClubApprovalServiceImplBase {
    private final ClubDAO clubDAO;
    private final ClubMembershipDAO clubMembershipDAO;

    public ClubApprovalController(ClubDAO clubDAO, ClubMembershipDAO clubMembershipDAO) {
        this.clubDAO = clubDAO;
        this.clubMembershipDAO = clubMembershipDAO;
    }

    @Override
    public void submitClubForApproval(SubmitClubForApprovalRequest request,
                                      StreamObserver<ClubApprovalActionResponse> responseObserver) {
        try {
            Club club = new Club();
            club.setName(request.getNewClub().getName());
            club.setDescription(request.getNewClub().getDescription());
            club.setCategory(request.getNewClub().getCategory());
            clubDAO.insertClub(club, Integer.parseInt(request.getSubmitter().getUserId()));
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
            int submittedId = clubDAO.getSubmittedByForClub(Integer.parseInt(request.getClubId()));
            clubMembershipDAO.createMembership(
                    submittedId,
                    Integer.parseInt(request.getClubId()),
                    true);
            responseObserver.onNext(ClubApprovalActionResponse.newBuilder()
                    .setSuccess(success)
                    .build());
            responseObserver.onCompleted();

            var notification = NotificationServiceProto.NotificationMessage.newBuilder()
                    .setNotificationId(UUID.randomUUID().toString())
                    .setUserId(String.valueOf(submittedId))
                    .setMessage("Your club has been approved!")
                    .setType(NotificationServiceProto.NotificationType.CLUB_APPROVED)
                    .setTimestamp(Instant.now().toString())
                    .setIsRead(false)
                    .build();

            NotificationService.getInstance()
                    .notifyObservers(List.of(String.valueOf(submittedId)), notification);

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

            int submittedId = clubDAO.getSubmittedByForClub(Integer.parseInt(request.getClubId()));
            var notification = NotificationServiceProto.NotificationMessage.newBuilder()
                    .setNotificationId(UUID.randomUUID().toString())
                    .setUserId(String.valueOf(submittedId))
                    .setMessage("Your club has been rejected.")
                    .setType(NotificationServiceProto.NotificationType.CLUB_REJECTED)
                    .setTimestamp(Instant.now().toString())
                    .setIsRead(false)
                    .build();

            NotificationService.getInstance()
                    .notifyObservers(List.of(String.valueOf(submittedId)), notification);

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
