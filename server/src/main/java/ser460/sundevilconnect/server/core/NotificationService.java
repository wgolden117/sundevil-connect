package ser460.sundevilconnect.server.core;

import io.grpc.stub.StreamObserver;
import java.util.logging.Level;
import ser460.sundevilconnect.server.notifications.NotificationDAO;
import ser460.sundevilconnect.shared.proto.NotificationServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.NotificationServiceProto.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class NotificationService extends NotificationServiceImplBase {
    private static NotificationService instance = new NotificationService();

    private static final Logger logger =
            Logger.getLogger(NotificationService.class.getName());

    // we're using StreamObservers for our observers, since it is an open stream
    // to the client UI elements that will be notified
    private final Map<String, List<StreamObserver<NotificationMessage>>> observers
            =  new ConcurrentHashMap<>();

    private final NotificationDAO notificationDAO;

    private NotificationService() {
        this.notificationDAO = new NotificationDAO(DatabaseService.getInstance());
    }

    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    @Override
    public void subscribe(SubscribeRequest request,
                          StreamObserver<NotificationMessage> responseObserver) {

        attach(responseObserver, request.getUserId());

        // Load past notifications from DB
        List<NotificationMessage> pastNotifications =
                notificationDAO.getNotificationsForUser(request.getUserId());

        for (NotificationMessage notification : pastNotifications) {
            // send to client
            responseObserver.onNext(notification);
        }
    }

    @Override
    public void unsubscribe(UnsubscribeRequest request,
                            StreamObserver<UnsubscribeResponse> responseObserver) {
        detachAll(request.getUserId());
        responseObserver.onNext(UnsubscribeResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    public void attach(StreamObserver<NotificationMessage> observer, String userId) {
        observers.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(observer);
    }

    public void detachAll(String userId) {
        observers.remove(userId);
    }

    public void notifyObservers(List<String> userIds, NotificationMessage notification) {

        for (String userId : userIds) {

            // 🔥 Create a NEW notification per user
            NotificationMessage userNotification = NotificationMessage.newBuilder()
                    .setNotificationId(java.util.UUID.randomUUID().toString())
                    .setUserId(userId)
                    .setMessage(notification.getMessage())
                    .setType(notification.getType())
                    .setTimestamp(notification.getTimestamp())
                    .setIsRead(false)
                    .build();

            // Save per-user notification
            notificationDAO.saveNotification(userNotification);

            // Send to live observers
            List<StreamObserver<NotificationMessage>> userObservers = observers.get(userId);

            if (userObservers != null) {
                for (StreamObserver<NotificationMessage> observer : userObservers) {
                    try {
                        observer.onNext(userNotification);
                    } catch (Exception e) {
                        userObservers.remove(observer);
                    }
                }
            }
        }
    }

    @Override
    public void markAsRead(MarkAsReadRequest request,
                           StreamObserver<MarkAsReadResponse> responseObserver) {

        try {
            notificationDAO.markAsRead(request.getNotificationId());

            responseObserver.onNext(
                    MarkAsReadResponse.newBuilder()
                            .setSuccess(true)
                            .build()
            );
            responseObserver.onCompleted();

        } catch (Exception e) {

            logger.log(Level.SEVERE, "Failed to mark notification as read", e);

            responseObserver.onNext(
                    MarkAsReadResponse.newBuilder()
                            .setSuccess(false)
                            .build()
            );
            responseObserver.onCompleted();
        }
    }
}
