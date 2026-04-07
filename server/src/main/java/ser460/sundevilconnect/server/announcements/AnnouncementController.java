package ser460.sundevilconnect.server.announcements;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.server.clubs.ClubMembershipDAO;
import ser460.sundevilconnect.shared.proto.AnnouncementServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.AnnouncementServiceProto.*;

import java.sql.SQLException;
import java.util.List;

public class AnnouncementController extends AnnouncementServiceImplBase {
    private final AnnouncementDAO announcementDAO;
    private final ClubMembershipDAO clubMembershipDAO;

    public AnnouncementController(AnnouncementDAO announcementDAO, ClubMembershipDAO clubMembershipDAO) {
        this.announcementDAO = announcementDAO;
        this.clubMembershipDAO = clubMembershipDAO;
    }

    @Override
    public void createAnnouncement(CreateAnnouncementRequest request,
                                   StreamObserver<AnnouncementActionResponse> responseObserver) {
        try {
            int postedToId = Integer.parseInt(request.getAnnouncement().getPostedTo().getClubId());
            int createdById = Integer.parseInt(request.getAnnouncement().getCreatedBy().getUserId());
            String title = request.getAnnouncement().getTitle();
            String body = request.getAnnouncement().getBody();
            String status = request.getAnnouncement().getStatus();

            announcementDAO.createAnnouncement(postedToId, createdById, title, body, status);

            responseObserver.onNext(AnnouncementActionResponse.newBuilder().setSuccess(true).build());
            responseObserver.onCompleted();

        } catch (SQLException e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException());
        }
    }

    @Override
    public void editAnnouncement(EditAnnouncementRequest request,
                                 StreamObserver<AnnouncementActionResponse> responseObserver) {
        try {
            int announcementId = Integer.parseInt(request.getEditedAnnouncement().getAnnouncementId());
            String title = request.getEditedAnnouncement().getTitle();
            String body = request.getEditedAnnouncement().getBody();
            String status = request.getEditedAnnouncement().getStatus();

            boolean updated = announcementDAO.editAnnouncement(announcementId, title, body, status);

            if (!updated) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Announcement not found")
                        .asException());
                return;
            }
            responseObserver.onNext(AnnouncementActionResponse.newBuilder().setSuccess(true).build());
            responseObserver.onCompleted();

        } catch (SQLException e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asException());
        }
    }

    @Override
    public void deleteAnnouncement(DeleteAnnouncementRequest request,
                                   StreamObserver<AnnouncementActionResponse> responseObserver) {
        try {
            int announcementId = Integer.parseInt(request.getAnnouncementId());

            boolean updated = announcementDAO.deleteAnnouncement(announcementId);

            if (!updated) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Announcement not found")
                        .asException());
                return;
            }
            responseObserver.onNext(AnnouncementActionResponse.newBuilder().setSuccess(true).build());
            responseObserver.onCompleted();

        } catch (SQLException e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asException());
        }
    }

    @Override
    public void publishAnnouncement(PublishAnnouncementRequest request,
                                    StreamObserver<AnnouncementActionResponse> responseObserver) {
        try {
            int announcementId = Integer.parseInt(request.getAnnouncementId());

            boolean published = announcementDAO.publishAnnouncement(announcementId, request.getIsPublished());

            if (!published) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Announcement not found")
                        .asException());
                return;
            }

            // TODO notify club members when event is published

            responseObserver.onNext(AnnouncementActionResponse.newBuilder().setSuccess(true).build());
            responseObserver.onCompleted();

        } catch (SQLException e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asException());
        }
    }

    @Override
    public void getAnnouncementsForClub(GetAnnouncementsForClubRequest request,
                                        StreamObserver<GetAnnouncementsResponse> responseObserver) {
        try {
            int clubId = Integer.parseInt(request.getClubId());
            int requestingUserId = Integer.parseInt(request.getRequestingUserId());

            boolean isLeader = clubMembershipDAO.getMembershipForStudent(requestingUserId)
                    .stream()
                    .anyMatch(m -> Integer.parseInt(m.getClub().getClubId()) == clubId
                            && "LEADER".equals(m.getRole()));

            List<Announcement> announcements = announcementDAO.getAnnouncementsForClub(clubId, isLeader);

            GetAnnouncementsResponse response = GetAnnouncementsResponse.newBuilder()
                    .addAllAnnouncements(announcements
                            .stream()
                            .map(AnnouncementDAO::toProto)
                            .toList())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (SQLException e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asException());
        }
    }
}
