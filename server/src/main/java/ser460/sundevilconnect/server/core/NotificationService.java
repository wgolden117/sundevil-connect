package ser460.sundevilconnect.server.core;

import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.shared.proto.NotificationServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.NotificationServiceProto.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationService extends NotificationServiceImplBase {
    private static NotificationService instance = new NotificationService();

    // we're using StreamObservers for our observers, since it is an open stream
    // to the client UI elements that will be notified
    private Map<String, List<StreamObserver<NotificationMessage>>> observers
            =  new ConcurrentHashMap<>();

    private NotificationService() {}

    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    @Override
    public void subscribe(SubscribeRequest request,
                          StreamObserver<NotificationMessage> responseObserver) {
        // TODO: validate token
        attach(responseObserver, request.getUserId());
        // TODO: this stream IS the observer connection
        // keep it open by not calling onCompleted().
        // TODO: query all user notifications and send the ones that were not delivered
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
        // TODO: for each userId in userIds, get their list of StreamObservers
        // TODO: for each StreamObserver, call onNext(notification) to push down the stream
        // TODO: if a stream is closed/cancelled, remove it from the map
        // this is the mechanism that replaces the hand-written Observer.update() call
    }
}
