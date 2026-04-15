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
            contentDAO.flagContent(Integer.parseInt(request.getContentId()), request.getReason());
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
