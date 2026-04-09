package ser460.sundevilconnect.client.test;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import ser460.sundevilconnect.shared.proto.EventBrowsingServiceGrpc;
import ser460.sundevilconnect.shared.proto.EventBrowsingServiceProto;

public class EventBrowsingTest {
    public static void main(String[] args) {

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        // Create stub
        EventBrowsingServiceGrpc.EventBrowsingServiceBlockingStub eventStub =
                EventBrowsingServiceGrpc.newBlockingStub(channel);

        System.out.println("\n--- Fetching all events ---");

        // Call GetAllEvents
        EventBrowsingServiceProto.EventListResponse response =
                eventStub.getAllEvents(
                        EventBrowsingServiceProto.GetAllEventsRequest.newBuilder().build()
                );

        // Print results
        System.out.println("Events returned:");
        response.getEventsList().forEach(System.out::println);

        channel.shutdown();
    }
}