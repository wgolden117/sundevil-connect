package ser460.sundevilconnect.server.admin;

import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.shared.proto.ContentModerationServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.ContentModerationServiceProto.*;

import java.util.List;

public class ContentModerationController extends ContentModerationServiceImplBase {
    private List<Content> flaggedContent;

    @Override
    public void flagContent(FlagContentRequest request,
                            StreamObserver<ContentActionResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(ContentActionResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void approveFlaggedContent(ApproveFlaggedContentRequest request,
                                      StreamObserver<ContentActionResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(ContentActionResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void removeFlaggedContent(RemoveFlaggedContentRequest request,
                                     StreamObserver<ContentActionResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(ContentActionResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void getFlaggedContent(GetFlaggedContentRequest request,
                                  StreamObserver<GetFlaggedContentResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(GetFlaggedContentResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    // these don't have proto service definitions yet, because I'm not sure if they
    // make sense server-side and/or might be redundant.
    public void reviewFlaggedContent(Content content) {}
    public void unflagContent(Content content) {}
}
