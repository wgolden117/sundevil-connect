package ser460.sundevilconnect.server.admin;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.shared.proto.ContentModerationServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.ContentModerationServiceProto.*;

import java.util.List;

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

            // TODO send notification to flagged user

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

            // TODO send notification to flagged user

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException());
        }
    }

    @Override
    public void removeFlaggedContent(RemoveFlaggedContentRequest request,
                                     StreamObserver<ContentActionResponse> responseObserver) {
        try {
            contentDAO.removeContent(Integer.parseInt(request.getContentId()));
            responseObserver.onNext(ContentActionResponse.newBuilder()
                    .setSuccess(true)
                    .build());
            responseObserver.onCompleted();

            // TODO send notification to flagged user

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
