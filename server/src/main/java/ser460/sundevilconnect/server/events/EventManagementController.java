package ser460.sundevilconnect.server.events;

import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.shared.proto.EventManagementServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.EventManagementServiceProto.*;


public class EventManagementController extends EventManagementServiceImplBase {

    private final EventManagementDAO eventManagementDAO = new EventManagementDAO();
    private final EventRegistrationDAO eventRegistrationDAO = new EventRegistrationDAO();

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

        // Send notifications
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
    }

    @Override
    public void cancelEvent(CancelEventRequest request,
                            StreamObserver<EventManagementActionResponse> responseObserver) {

        int eventId = Integer.parseInt(request.getEventId());

        boolean success = eventManagementDAO.cancelEvent(eventId);

        // Send notifications
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
