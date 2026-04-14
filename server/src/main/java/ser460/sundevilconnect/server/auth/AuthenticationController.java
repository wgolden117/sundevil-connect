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

        String sessionToken = authService.authenticateUser(
                request.getEmail(),
                request.getPassword()
        );

        if (sessionToken != null) {
            User user = authService.getSessionUser(sessionToken);

            responseObserver.onNext(LoginResponse.newBuilder()
                    .setSuccess(true)
                    .setUser(UserSummary.newBuilder()
                            .setDisplayName(user.getFirstName() + " " + user.getLastName())
                            .setUserId(user.getUserId())
                            .build())
                    .setToken(sessionToken)
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
        // invalidate session
        boolean success = AuthenticationService.getInstance().logout(request.getToken());

        // TODO: call NotificationService.getInstance().detachAll(userId)

        responseObserver.onNext(LogoutResponse.newBuilder()
                .setSuccess(success)
                .build());

        responseObserver.onCompleted();
    }

    public void resetPassword(String email) {}
    private boolean validateCredentials(String username, String password) { return false; }

    // TODO figure out how we want to create new Students/Admins etc. We'll have to add them to the proto file
    // and then also add the service methods in here.
}
