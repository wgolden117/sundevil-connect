package ser460.sundevilconnect.server.events;

import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.shared.proto.EventManagementServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.EventManagementServiceProto.*;


public class EventManagementController extends EventManagementServiceImplBase {

    @Override
    public void createEvent(CreateEventRequest request,
                            StreamObserver<EventManagementActionResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(EventManagementActionResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateEvent(UpdateEventRequest request,
                            StreamObserver<EventManagementActionResponse> responseObserver) {
        // TODO: notify registered students via NotificationService - EVENT_UPDATED
        responseObserver.onNext(EventManagementActionResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void cancelEvent(CancelEventRequest request,
                            StreamObserver<EventManagementActionResponse> responseObserver) {
        // TODO: notify registered students via NotificationService - EVENT_UPDATED
        responseObserver.onNext(EventManagementActionResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void getEventsForClub(GetEventsForClubRequest request,
                                 StreamObserver<GetEventsForClubResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(GetEventsForClubResponse.newBuilder().build());
        responseObserver.onCompleted();
    }
}
