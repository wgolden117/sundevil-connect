package ser460.sundevilconnect.shared.notifications;

public interface Observer {
    // todo: this method call crosses a network boundary,
    // will need to implement via gRPC streaming or raw socket
    // when client receives a notification, this is what gets triggered
    public void update(Notification notification);
}
