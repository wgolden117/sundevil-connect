package ser460.sundevilconnect.server.auth;

import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.shared.proto.AuthServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.AuthServiceProto.*;

public class AuthenticationController extends AuthServiceImplBase {

    @Override
    public void login(LoginRequest request,
                      StreamObserver<LoginResponse> responseObserver) {
        // TODO: validate credentials via AuthenticationService
        // TODO: create session, return token and role
        responseObserver.onNext(LoginResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void logout(LogoutRequest request,
                       StreamObserver<LogoutResponse> responseObserver) {
        // TODO: invalidate session via AuthenticationService
        // TODO: call NotificationService.getInstance().detachAll(userId)
        responseObserver.onNext(LogoutResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    public void resetPassword(String email) {}
    private boolean validateCredentials(String username, String password) { return false; }

    // TODO figure out how we want to create new Students/Admins etc. We'll have to add them to the proto file
    // and then also add the service methods in here.
}
