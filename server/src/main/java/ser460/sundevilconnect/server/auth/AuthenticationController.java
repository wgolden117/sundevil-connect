package ser460.sundevilconnect.server.auth;

import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.server.core.AuthenticationService;
import ser460.sundevilconnect.shared.proto.AuthServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.AuthServiceProto.*;
import ser460.sundevilconnect.shared.proto.EntitiesProto.*;

public class AuthenticationController extends AuthServiceImplBase {

    @Override
    public void login(LoginRequest request,
                      StreamObserver<LoginResponse> responseObserver) {

        AuthenticationService authService = AuthenticationService.getInstance();

        User user = authService.authenticateUser(
                request.getEmail(),
                request.getPassword()
        );

        if (user != null) {
            String token = authService.createSession(user);

            responseObserver.onNext(LoginResponse.newBuilder()
                    .setSuccess(true)
                    .setUser(UserSummary.newBuilder()
                            .setDisplayName(user.getEmail())
                            .setUserId(user.getUserId())
                            .build())
                    .setToken(token)
                    .setRole(user.getRole())
                    .build());

        } else {
            responseObserver.onNext(LoginResponse.newBuilder()
                    .setSuccess(false)
                    .build());
        }

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
