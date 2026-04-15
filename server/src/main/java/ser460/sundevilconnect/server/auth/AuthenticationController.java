package ser460.sundevilconnect.server.auth;

import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.server.core.AuthenticationService;
import ser460.sundevilconnect.server.core.NotificationService;
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
            String displayName = user.getFirstName() + " " + user.getLastName();
            responseObserver.onNext(LoginResponse.newBuilder()
                    .setSuccess(true)
                    .setUser(UserSummary.newBuilder()
                            .setDisplayName(displayName)
                            .setUserId(user.getUserId())
                            .build())
                    .setToken(sessionToken)
                    .setRole(user.getRole())
                    .build());
            System.out.printf(":: LOGIN :: \n\t%s\n\t%s\n", user.getUserId(), sessionToken);
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

        NotificationService.getInstance().detachAll(request.getUserId());

        responseObserver.onNext(LogoutResponse.newBuilder()
                .setSuccess(success)
                .build());
        System.out.printf(":: LOGOUT :: \n\t%s\n\t%s\n", request.getUserId(), request.getToken());
        responseObserver.onCompleted();
    }

    // TODO figure out how we want to create new Students/Admins etc. We'll have to add them to the proto file
    // and then also add the service methods in here.
}
