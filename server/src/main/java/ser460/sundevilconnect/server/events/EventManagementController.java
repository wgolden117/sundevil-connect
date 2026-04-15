package ser460.sundevilconnect.server.events;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.shared.proto.EventManagementServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.EventManagementServiceProto.*;


public class EventManagementController extends EventManagementServiceImplBase {

    private final EventManagementDAO eventManagementDAO;

    public EventManagementController(EventManagementDAO eventManagementDAO) {
        this.eventManagementDAO = eventManagementDAO;
    }

    @Override
    public void createEvent(CreateEventRequest request,
                            StreamObserver<EventManagementActionResponse> responseObserver) {
        try {
            boolean success = eventManagementDAO.createEvent(
                    request.getEvent(),
                    Integer.parseInt(request.getUserId()));
            responseObserver.onNext(
                    EventManagementActionResponse.newBuilder()
                            .setSuccess(success)
                            .build()
            );
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException());
        }
    }

    @Override
    public void updateEvent(UpdateEventRequest request,
                            StreamObserver<EventManagementActionResponse> responseObserver) {
        try {
            boolean success = eventManagementDAO.updateEvent(request.getUpdatedEvent());
            responseObserver.onNext(
                    EventManagementActionResponse.newBuilder()
                            .setSuccess(success)
                            .build()
            );
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException());
        }
    }

    @Override
    public void cancelEvent(CancelEventRequest request,
                            StreamObserver<EventManagementActionResponse> responseObserver) {
        try {
            boolean success = eventManagementDAO.cancelEvent(Integer.parseInt(request.getEventId()));
            responseObserver.onNext(
                    EventManagementActionResponse.newBuilder()
                            .setSuccess(success)
                            .build()
            );
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException());
        }
    }

    @Override
    public void getEventsForClub(GetEventsForClubRequest request,
                                 StreamObserver<GetEventsForClubResponse> responseObserver) {
        try {
            GetEventsForClubResponse response = GetEventsForClubResponse.newBuilder()
                    .addAllEvents(eventManagementDAO.getEventsForClub(request.getClubId()))
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asException());
        }
    }
}
