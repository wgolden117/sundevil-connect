package ser460.sundevilconnect.client;

import javafx.application.Application;
import javafx.stage.Stage;
import ser460.sundevilconnect.shared.proto.AuthServiceProto;

public class Main extends Application {

    @Override
    public void init() throws Exception {
        // Only start embedded server if nothing is already on port 8080
        try (java.net.Socket socket = new java.net.Socket("localhost", 8080)) {
            System.out.println("External server detected, skipping embedded server startup.");
        } catch (java.io.IOException e) {
            // Nothing running on 8080, start embedded server
            Thread serverThread = new Thread(() -> {
                try {
                    ser460.sundevilconnect.server.Main.main(new String[0]);
                } catch (Exception ex) {
                    System.err.println("Server failed to start:");
                    ex.printStackTrace();
                }
            }, "grpc-server-thread");
            serverThread.setDaemon(true);
            serverThread.start();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        boolean connected = false;

        for (int i = 0; i < 10; i++) {
            try {
                System.out.println("Attempting to connect to server...");
                ConnectionManager.getInstance().connect("localhost", 8080);
                connected = true;
                System.out.println("Connected to server!");
                break;
            } catch (Exception e) {
                System.err.println("Server not ready yet, retrying...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            }
        }

        if (!connected) {
            System.err.println("Could not connect to server after multiple attempts.");
        }

        SceneController sceneController = SceneController.getInstance();
        primaryStage.setTitle("Sundevil Connect");

        sceneController.setStage(primaryStage);
        sceneController.changeSceneToLogin();

        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        try {
            ConnectionManager.getInstance().getAuthStub().logout(AuthServiceProto.LogoutRequest.newBuilder()
                    .setUserId(CurrentUser.getInstance().getUserId())
                    .setToken(CurrentUser.getInstance().getSessionToken())
                    .build());
        } catch (Exception ignored) {}
        ConnectionManager.getInstance().shutdown();
        System.exit(0); // force exit since gRPC server thread is running
    }

    public static void main(String[] args) {
        launch(args);
    }
}