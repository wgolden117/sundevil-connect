package ser460.sundevilconnect.server.events;

import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.server.events.filter.FilterStrategy;
import ser460.sundevilconnect.server.events.filter.FilterStrategyFactory;
import ser460.sundevilconnect.shared.proto.EventBrowsingServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.EventBrowsingServiceProto.*;

import java.util.List;

public class EventBrowsingController extends EventBrowsingServiceImplBase {

    @Override
    public void getAllEvents(GetAllEventsRequest request,
                             StreamObserver<EventListResponse> responseObserver) {

        var events = ser460.sundevilconnect.server.core.DatabaseService
                .getInstance()
                .getAllEvents();

        responseObserver.onNext(
                EventListResponse.newBuilder()
                        .addAllEvents(events)
                        .build()
        );

        responseObserver.onCompleted();
    }

    @Override
    public void getEventsByCategory(GetEventsByCategoryRequest request,
                                    StreamObserver<EventListResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(EventListResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void searchEvents(SearchEventsRequest request,
                             StreamObserver<EventListResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(EventListResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void getEventDetails(GetEventDetailsRequest request,
                                StreamObserver<EventDetailsResponse> responseObserver) {
        // TODO: implement
        responseObserver.onNext(EventDetailsResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void getFilteredEvents(GetFilteredEventsRequest request,
                                  StreamObserver<EventListResponse> responseObserver) {
        // TODO: use FilterStrategyFactory to construct strategies from request.getFiltersList()
        List<FilterStrategy> filterStrategies = FilterStrategyFactory.createStrategies(request.getFiltersList());

        // TODO: apply strategies sequentially to event list

        responseObserver.onNext(EventListResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    // these seem sort of out of place here and seem like they should be in the UI representation
    public Event getEventDetails() { return null; }
    public List<Event> sortEventsByDate() { return null; }
    public List<Event> sortEventsByPopularity() { return null; }
}
