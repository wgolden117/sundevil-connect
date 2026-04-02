package ser460.sundevilconnect.server.events;

import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.server.auth.Student;
import ser460.sundevilconnect.shared.proto.EventRegistrationServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.EventRegistrationServiceProto;
import ser460.sundevilconnect.shared.proto.EventRegistrationServiceProto.*;
import java.util.HashSet;
import java.util.Set;

public class EventRegistrationController extends EventRegistrationServiceImplBase {

    private Set<String> registrations = java.util.Collections.synchronizedSet(new HashSet<>());

    @Override
    public void registerStudentForEvent(RegisterStudentForEventRequest request,
                                        StreamObserver<RegisterStudentForEventResponse> responseObserver) {

        String studentId = request.getStudentId();
        String eventId = request.getEventId();

        System.out.println("[REGISTER] student=" + studentId + " event=" + eventId);

        // 🔥 TEMP: simulate capacity check
        boolean atCapacity = false;

        if (atCapacity) {
            RegisterStudentForEventResponse response =
                    RegisterStudentForEventResponse.newBuilder()
                            .setEventRegistration(
                                    EventRegistrationServiceProto.EventRegistration.newBuilder()
                                            .setStatus("FULL")
                                            .build()
                            )
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        String key = studentId + "_" + eventId;

        if (registrations.contains(key)) {
            System.out.println("Duplicate registration prevented");

            RegisterStudentForEventResponse response =
                    RegisterStudentForEventResponse.newBuilder()
                            .setEventRegistration(
                                    EventRegistrationServiceProto.EventRegistration.newBuilder()
                                            .setStatus("ALREADY_REGISTERED")
                                            .build()
                            )
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        registrations.add(key);

        EventRegistrationServiceProto.EventRegistration registration =
                EventRegistrationServiceProto.EventRegistration.newBuilder()
                        .setRegistrationId("reg_" + System.currentTimeMillis())
                        .setRegistrationDate(String.valueOf(System.currentTimeMillis()))
                        .setStatus("REGISTERED")
                        .build();

        RegisterStudentForEventResponse response =
                RegisterStudentForEventResponse.newBuilder()
                        .setEventRegistration(registration)
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void cancelRegistration(CancelRegistrationRequest request,
                                   StreamObserver<CancelRegistrationResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(CancelRegistrationResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void getRegistrationsForStudent(GetRegistrationsForStudentRequest request,
                                           StreamObserver<GetRegistrationsResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(GetRegistrationsResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void getRegistrationsForEvent(GetRegistrationsForEventRequest request,
                                         StreamObserver<GetRegistrationsResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(GetRegistrationsResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void checkEventCapacity(CheckEventCapacityRequest request,
                                   StreamObserver<CheckEventCapacityResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(CheckEventCapacityResponse.newBuilder().build());
        responseObserver.onCompleted();
    }
}
