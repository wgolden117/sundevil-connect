package ser460.sundevilconnect.server.events;

import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.server.auth.Student;
import ser460.sundevilconnect.shared.proto.EventRegistrationServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.EventRegistrationServiceProto.*;

public class EventRegistrationController extends EventRegistrationServiceImplBase {

    @Override
    public void registerStudentForEvent(RegisterStudentForEventRequest request,
                                        StreamObserver<RegisterStudentForEventResponse> responseObserver) {
        // TODO: check capacity via checkEventCapacity before registering
        // TODO: notify student via NotificationService - EVENT_REGISTRATION_CONFIRMED
        responseObserver.onNext(RegisterStudentForEventResponse.newBuilder().build());
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
