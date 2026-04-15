package ser460.sundevilconnect.server.admin;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.server.core.NotificationService;
import ser460.sundevilconnect.shared.proto.ContentModerationServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.ContentModerationServiceProto.*;
import ser460.sundevilconnect.shared.proto.EntitiesProto;
import ser460.sundevilconnect.shared.proto.NotificationServiceProto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ContentModerationController extends ContentModerationServiceImplBase {
    private final ContentDAO contentDAO;

    public ContentModerationController(ContentDAO contentDAO) {
        this.contentDAO = contentDAO;
    }

    @Override
    public void flagContent(FlagContentRequest request,
                            StreamObserver<ContentActionResponse> responseObserver) {
        try {
            int contentId;
            if (!request.getContentId().isEmpty()) {
                contentId = Integer.parseInt(request.getContentId());
            } else if (!request.getEventId().isEmpty()) {
                contentId = contentDAO.getContentIdByEventId(Integer.parseInt(request.getEventId()));
            } else if (!request.getAnnouncementId().isEmpty()) {
                contentId = contentDAO.getContentIdByAnnouncementId(Integer.parseInt(request.getAnnouncementId()));
            } else {
                throw new IllegalArgumentException("FlagContentRequest must provide contentId, eventId, or announcementId");
            }
            contentDAO.flagContent(contentId, request.getReason());
            responseObserver.onNext(ContentActionResponse.newBuilder().setSuccess(true).build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException());
        }
    }

    @Override
    public void approveFlaggedContent(ApproveFlaggedContentRequest request,
                                      StreamObserver<ContentActionResponse> responseObserver) {
        try {
            contentDAO.approveContent(Integer.parseInt(request.getContentId()));
            responseObserver.onNext(ContentActionResponse.newBuilder()
                    .setSuccess(true)
                    .build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException());
        }
    }

    @Override
    public void removeFlaggedContent(RemoveFlaggedContentRequest request,
                                     StreamObserver<ContentActionResponse> responseObserver) {
        try {
            EntitiesProto.Content content = contentDAO.getContentById(Integer.parseInt(request.getContentId()));
            String title = content.hasEvent() ? content.getEvent().getTitle() : content.getAnnouncement().getTitle();

            contentDAO.removeContent(Integer.parseInt(request.getContentId()));
            responseObserver.onNext(ContentActionResponse.newBuilder().setSuccess(true).build());
            responseObserver.onCompleted();

            var notification = NotificationServiceProto.NotificationMessage.newBuilder()
                    .setNotificationId(UUID.randomUUID().toString())
                    .setUserId(content.getCreatedBy().getUserId())
                    .setMessage("Your content \"" + title + "\" has been removed by an administrator.")
                    .setType(NotificationServiceProto.NotificationType.CONTENT_REMOVED)
                    .setTimestamp(Instant.now().toString())
                    .setIsRead(false)
                    .build();

            NotificationService.getInstance()
                    .notifyObservers(List.of(content.getCreatedBy().getUserId()), notification);

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException());
        }
    }

    @Override
    public void getContentFlagStatus(GetContentFlagStatusRequest request,
                                     StreamObserver<GetContentFlagStatusResponse> responseObserver) {
        try {
            boolean isFlagged = contentDAO.getFlagStatus(
                    request.getContentId(),
                    request.getEventId(),
                    request.getAnnouncementId());
            responseObserver.onNext(GetContentFlagStatusResponse.newBuilder()
                    .setIsFlagged(isFlagged)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException());
        }
    }

    @Override
    public void getFlaggedContent(GetFlaggedContentRequest request,
                                  StreamObserver<GetFlaggedContentResponse> responseObserver) {
        try {
            responseObserver.onNext(GetFlaggedContentResponse.newBuilder()
                    .addAllFlaggedContent(contentDAO.getFlaggedContent())
                    .build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException());
        }
    }
}
