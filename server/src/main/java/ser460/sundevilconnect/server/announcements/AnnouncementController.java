package ser460.sundevilconnect.server.announcements;

import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.shared.proto.AnnouncementServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.AnnouncementServiceProto.*;

public class AnnouncementController extends AnnouncementServiceImplBase {

    @Override
    public void createAnnouncement(CreateAnnouncementRequest request,
                                   StreamObserver<AnnouncementActionResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(AnnouncementActionResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void editAnnouncement(EditAnnouncementRequest request,
                                 StreamObserver<AnnouncementActionResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(AnnouncementActionResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void deleteAnnouncement(DeleteAnnouncementRequest request,
                                   StreamObserver<AnnouncementActionResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(AnnouncementActionResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void publishAnnouncement(PublishAnnouncementRequest request,
                                    StreamObserver<AnnouncementActionResponse> responseObserver) {
        // TODO: implement
        // TODO: notify students attached to clubs that an announcement has been posted
        responseObserver.onNext(AnnouncementActionResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void getAnnouncementsForClub(GetAnnouncementsForClubRequest request,
                                        StreamObserver<GetAnnouncementsResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(GetAnnouncementsResponse.newBuilder().build());
        responseObserver.onCompleted();
    }
}
