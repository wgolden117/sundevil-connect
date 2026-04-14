package ser460.sundevilconnect.server.events;

import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.shared.proto.EventRegistrationServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.EventRegistrationServiceProto;
import ser460.sundevilconnect.shared.proto.EventRegistrationServiceProto.*;
import ser460.sundevilconnect.shared.proto.EventRegistrationServiceProto.EventRegistration;
import ser460.sundevilconnect.shared.proto.EntitiesProto.Event;
import ser460.sundevilconnect.shared.proto.EventRegistrationServiceProto.GetRegistrationsResponse;
import ser460.sundevilconnect.shared.proto.EventRegistrationServiceProto.CancelRegistrationResponse;
import ser460.sundevilconnect.shared.proto.EventRegistrationServiceProto.RegisterStudentForEventResponse;
import ser460.sundevilconnect.server.core.NotificationService;
import ser460.sundevilconnect.shared.proto.NotificationServiceProto.NotificationMessage;
import ser460.sundevilconnect.shared.proto.NotificationServiceProto.NotificationType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class EventRegistrationController extends EventRegistrationServiceImplBase {

    private final EventRegistrationDAO eventRegistrationDAO;

    public EventRegistrationController(EventRegistrationDAO eventRegistrationDAO) {
        this.eventRegistrationDAO = eventRegistrationDAO;
    }

    @Override
    public void registerStudentForEvent(RegisterStudentForEventRequest request,
                                        StreamObserver<RegisterStudentForEventResponse> responseObserver) {

        String studentId = request.getStudentId();
        String eventId = request.getEventId();

        System.out.println("[REGISTER] student=" + studentId + " event=" + eventId);

        int studentIdInt = Integer.parseInt(studentId);
        int eventIdInt = Integer.parseInt(eventId);

        // 1. Check capacity
        if (eventRegistrationDAO.isEventAtCapacity(eventIdInt)) {
            responseObserver.onNext(
                    RegisterStudentForEventResponse.newBuilder()
                            .setSuccess(false)
                            .setMessage("Event is at full capacity")
                            .build()
            );
            responseObserver.onCompleted();
            return;
        }

        // 2. Check duplicate in DB
        if (eventRegistrationDAO.isStudentAlreadyRegistered(studentIdInt, eventIdInt)) {

            responseObserver.onNext(
                    RegisterStudentForEventResponse.newBuilder()
                            .setSuccess(false)
                            .setMessage("You are already registered for this event")
                            .build()
            );
            responseObserver.onCompleted();
            return;
        }

        // 3. Insert into DB
        long now = System.currentTimeMillis();

        boolean success = eventRegistrationDAO.registerStudent(studentIdInt, eventIdInt, now);

        if (!success) {
            responseObserver.onNext(
                    RegisterStudentForEventResponse.newBuilder()
                            .setSuccess(false)
                            .setMessage("Registration failed")
                            .build()
            );
            responseObserver.onCompleted();
            return;
        }

        //  Get event title (quick lookup)
        Event event = eventRegistrationDAO.getEventById(eventIdInt);
        String eventTitle = (event != null) ? event.getTitle() : "this event";

                // Send notification
        NotificationMessage notification = NotificationMessage.newBuilder()
                .setNotificationId(UUID.randomUUID().toString())
                .setUserId(studentId)
                .setMessage("You successfully registered for " + eventTitle + "!")
                .setType(NotificationType.EVENT_REGISTRATION_CONFIRMED)
                .setTimestamp(Instant.now().toString())
                .setIsRead(false)
                .build();

        NotificationService.getInstance().notifyObservers(
                List.of(studentId),
                notification
        );

        // 4. Build success response
        EventRegistrationServiceProto.EventRegistration registration =
                EventRegistrationServiceProto.EventRegistration.newBuilder()
                        .setRegistrationId(String.valueOf(now))
                        .setRegistrationDate(String.valueOf(now))
                        .setStatus("REGISTERED")
                        .build();

        responseObserver.onNext(
                RegisterStudentForEventResponse.newBuilder()
                        .setSuccess(true)
                        .setMessage("Registered successfully")
                        .setEventRegistration(registration)
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void cancelRegistration(CancelRegistrationRequest request,
                                   StreamObserver<CancelRegistrationResponse> responseObserver) {

        int registrationId = Integer.parseInt(request.getRegistrationId());

        // STEP 1: get registration before deleting
        EventRegistration registration =
                eventRegistrationDAO.getRegistrationById(registrationId);

        boolean success = eventRegistrationDAO.cancelRegistration(registrationId);

        if (success && registration != null) {

            String studentId = registration.getStudent().getUserId();
            String eventTitle = registration.getEvent().getTitle();

            // STEP 2: build notification
            NotificationMessage notification = NotificationMessage.newBuilder()
                    .setNotificationId(java.util.UUID.randomUUID().toString())
                    .setUserId(studentId)
                    .setMessage("You canceled your registration for " + eventTitle)
                    .setType(NotificationType.EVENT_UPDATED)
                    .setTimestamp(java.time.Instant.now().toString())
                    .setIsRead(false)
                    .build();

            // STEP 3: send it
            NotificationService.getInstance().notifyObservers(
                    java.util.List.of(studentId),
                    notification
            );
        }

        responseObserver.onNext(
                CancelRegistrationResponse.newBuilder()
                        .setSuccess(success)
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void getRegistrationsForStudent(
            GetRegistrationsForStudentRequest request,
            StreamObserver<GetRegistrationsResponse> responseObserver) {

        int studentId = Integer.parseInt(request.getStudentId());

        List<EventRegistration> registrations =
                eventRegistrationDAO.getRegistrationsForStudent(studentId);

        GetRegistrationsResponse response =
                GetRegistrationsResponse.newBuilder()
                        .addAllRegistrations(registrations)
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getRegistrationsForEvent(GetRegistrationsForEventRequest request,
                                         StreamObserver<GetRegistrationsResponse> responseObserver) {

        int eventId = Integer.parseInt(request.getEventId());

        List<EventRegistration> registrations =
                eventRegistrationDAO.getRegistrationsForEvent(eventId);

        GetRegistrationsResponse response =
                GetRegistrationsResponse.newBuilder()
                        .addAllRegistrations(registrations)
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
