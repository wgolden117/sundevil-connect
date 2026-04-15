package ser460.sundevilconnect.server.events;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.shared.proto.EventManagementServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.EventManagementServiceProto.*;


public class EventManagementController extends EventManagementServiceImplBase {

    private final EventManagementDAO eventManagementDAO;
    private final EventRegistrationDAO eventRegistrationDAO =
            new EventRegistrationDAO(ser460.sundevilconnect.server.core.DatabaseService.getInstance());

    public EventManagementController(EventManagementDAO eventManagementDAO) {
        this.eventManagementDAO = eventManagementDAO;
    }

    @Override
    public void createEvent(CreateEventRequest request,
                            StreamObserver<EventManagementActionResponse> responseObserver) {

        try {
            boolean success = eventManagementDAO.createEvent(request.getEvent());
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
            var event = request.getUpdatedEvent();

            boolean success = eventManagementDAO.updateEvent(event);

            if (success) {
                int eventId = Integer.parseInt(event.getEventId());
                String eventTitle = event.getTitle();

                var registrations = eventRegistrationDAO.getRegistrationsForEvent(eventId);

                java.util.List<String> userIds = new java.util.ArrayList<>();
                registrations.forEach(r ->
                        userIds.add(r.getStudent().getUserId())
                );

                var notification = ser460.sundevilconnect.shared.proto.NotificationServiceProto.NotificationMessage.newBuilder()
                        .setNotificationId(java.util.UUID.randomUUID().toString())
                        .setMessage("Event updated: " + eventTitle)
                        .setType(ser460.sundevilconnect.shared.proto.NotificationServiceProto.NotificationType.EVENT_UPDATED)
                        .setTimestamp(java.time.Instant.now().toString())
                        .setIsRead(false)
                        .build();

                ser460.sundevilconnect.server.core.NotificationService.getInstance()
                        .notifyObservers(userIds, notification);
            }

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
            int eventId = Integer.parseInt(request.getEventId());

            boolean success = eventManagementDAO.cancelEvent(eventId);

            if (success) {
                var registrations = eventRegistrationDAO.getRegistrationsForEvent(eventId);

                java.util.List<String> userIds = new java.util.ArrayList<>();
                registrations.forEach(r ->
                        userIds.add(r.getStudent().getUserId())
                );

                var notification = ser460.sundevilconnect.shared.proto.NotificationServiceProto.NotificationMessage.newBuilder()
                        .setNotificationId(java.util.UUID.randomUUID().toString())
                        .setMessage("An event you registered for has been CANCELLED")
                        .setType(ser460.sundevilconnect.shared.proto.NotificationServiceProto.NotificationType.EVENT_UPDATED)
                        .setTimestamp(java.time.Instant.now().toString())
                        .setIsRead(false)
                        .build();

                ser460.sundevilconnect.server.core.NotificationService.getInstance()
                        .notifyObservers(userIds, notification);
            }

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
