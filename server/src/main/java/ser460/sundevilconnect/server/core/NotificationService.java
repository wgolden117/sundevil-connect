package ser460.sundevilconnect.server.core;

import ser460.sundevilconnect.shared.notifications.Notification;
import ser460.sundevilconnect.shared.notifications.Observer;
import ser460.sundevilconnect.server.notifications.Subject;

import java.util.List;
import java.util.Map;

public class NotificationService implements Subject {
    private static NotificationService instance;
    // right now its Map<String, List<Observer>>,
    // but really it'll end up more like Map<String, StreamObserver<NotificationProto>>
    // when we get gRPC streaming set up for notifications
    private Map<String, List<Observer>> observers;

    private NotificationService() {}

    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    @Override
    public void attach(Observer o, String userId) {}

    @Override
    public void detachAll(String userId) {}

    @Override
    public void notifyObservers(String userId, Notification notification) {}
}
