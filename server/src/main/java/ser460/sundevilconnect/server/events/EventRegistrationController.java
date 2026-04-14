package ser460.sundevilconnect.server.events;

import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.shared.proto.EventRegistrationServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.EventRegistrationServiceProto;
import ser460.sundevilconnect.shared.proto.EventRegistrationServiceProto.*;
import ser460.sundevilconnect.shared.proto.EventRegistrationServiceProto.EventRegistration;
import ser460.sundevilconnect.shared.proto.EventRegistrationServiceProto.GetRegistrationsResponse;
import ser460.sundevilconnect.shared.proto.EventRegistrationServiceProto.CancelRegistrationResponse;
import ser460.sundevilconnect.shared.proto.EventRegistrationServiceProto.RegisterStudentForEventResponse;
import java.util.List;

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

        boolean success = eventRegistrationDAO.cancelRegistration(registrationId);

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
