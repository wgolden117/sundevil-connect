package ser460.sundevilconnect.server.notifications;

import ser460.sundevilconnect.shared.notifications.Notification;
import ser460.sundevilconnect.shared.notifications.Observer;

public interface Subject {
    public void attach(Observer o, String userId);
    public void detachAll(String userId);
    public void notifyObservers(String userId, Notification notification);
}
