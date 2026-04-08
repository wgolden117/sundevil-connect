package ser460.sundevilconnect.server.events;

import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.shared.proto.EventManagementServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.EventManagementServiceProto.*;


public class EventManagementController extends EventManagementServiceImplBase {

    private final EventManagementDAO eventManagementDAO = new EventManagementDAO();

    @Override
    public void createEvent(CreateEventRequest request,
                            StreamObserver<EventManagementActionResponse> responseObserver) {

        var event = request.getEvent();

        boolean success = eventManagementDAO.createEvent(event);

        responseObserver.onNext(
                EventManagementActionResponse.newBuilder()
                        .setSuccess(success)
                        .build()
        );

        responseObserver.onCompleted();
    }

    @Override
    public void updateEvent(UpdateEventRequest request,
                            StreamObserver<EventManagementActionResponse> responseObserver) {

        var event = request.getUpdatedEvent();

        boolean success = eventManagementDAO.updateEvent(event);

        responseObserver.onNext(
                EventManagementActionResponse.newBuilder()
                        .setSuccess(success)
                        .build()
        );

        responseObserver.onCompleted();
    }

    @Override
    public void cancelEvent(CancelEventRequest request,
                            StreamObserver<EventManagementActionResponse> responseObserver) {

        int eventId = Integer.parseInt(request.getEventId());

        boolean success = eventManagementDAO.cancelEvent(eventId);

        responseObserver.onNext(
                EventManagementActionResponse.newBuilder()
                        .setSuccess(success)
                        .build()
        );

        responseObserver.onCompleted();
    }

    @Override
    public void getEventsForClub(GetEventsForClubRequest request,
                                 StreamObserver<GetEventsForClubResponse> responseObserver) {

        String clubId = request.getClubId();

        var events = eventManagementDAO.getEventsForClub(clubId);

        responseObserver.onNext(
                GetEventsForClubResponse.newBuilder()
                        .addAllEvents(events)
                        .build()
        );

        responseObserver.onCompleted();
    }
}
