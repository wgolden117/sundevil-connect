package ser460.sundevilconnect.client;

import javafx.application.Application;
import javafx.stage.Stage;
import ser460.sundevilconnect.shared.proto.AuthServiceProto;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneController sceneController = SceneController.getInstance();
        primaryStage.setTitle("Sundevil Connect");

        sceneController.setStage(primaryStage);
        sceneController.changeSceneToLogin();

        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        // ensure we attempt logout and close the connection before exit
        ConnectionManager.getInstance().getAuthStub().logout(AuthServiceProto.LogoutRequest.newBuilder()
                .setUserId(CurrentUser.getInstance().getUserId())
                .setToken(CurrentUser.getInstance().getSessionToken())
                .build());
        ConnectionManager.getInstance().shutdown();
    }

    public static void main(String[] args) {
        // create managed channel for grpc
        ConnectionManager.getInstance().connect("localhost", 8080);
        // launch ui
        launch(args);
    }
}
