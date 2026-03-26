package ser460.sundevilconnect.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auth/login.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("Sundevil Connect");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        // ensure we close the connection before exit
        ConnectionManager.getInstance().shutdown();
    }

    public static void main(String[] args) {
        // create managed channel for grpc
        ConnectionManager.getInstance().connect("localhost", 8080);
        // launch ui
        launch(args);
    }
}
