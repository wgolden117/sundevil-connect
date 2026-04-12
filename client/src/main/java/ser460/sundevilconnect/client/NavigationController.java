package ser460.sundevilconnect.client;

import ser460.sundevilconnect.client.main.MainController;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

public class NavigationController {
    private static NavigationController instance;
    private MainController mainController;

    private NavigationController() {}

    public static NavigationController getInstance() {
        if (instance == null) {
            instance = new NavigationController();
        }
        return instance;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void openClubPageTab(EntitiesProto.Club club) {
        if (mainController != null) {
            mainController.openClubPageTab(club);
        }
    }
}
