package ser460.sundevilconnect.server;

import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;

    // one instance of each controller for routing purposes need to
    // be added here

    public ClientHandler(Socket socket) {
        this.socket = socket;
        // todo: spin up Controller instances
    }

    @Override
    public void run() {
        // todo: deserialize incoming proto message
        // todo: route to appropriate controller based on proto module/message type
        // todo: consider using a oneof envelope message in proto for clean dispatch
        // todo: serialize and send response
        // todo: NotificationService.getInstance().detachAll(userId) on disconnect

        // for this to work effectively, we need a oneof envelope message in proto -
        // protobuf doesn't have inheritance but oneof acts as a discriminated union,
        // letting us define a top-level Request message with a oneof payload field
        // that can be any module-specific request type. ClientHandler switches on
        // that field to route to the correct controller cleanly.
        // Effectively its like polymorphism: we have a meta request that says
        // "I am _one_ of the following requests", and packages that request inside
        // of it, allowing the handler to take in one kind of request, open the
        // package, see what's inside and then deliver it to where it needs to go.
    }
}
