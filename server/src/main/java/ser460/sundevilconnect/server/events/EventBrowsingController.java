package ser460.sundevilconnect.server.events;

import io.grpc.stub.StreamObserver;
import ser460.sundevilconnect.server.events.filter.FilterStrategy;
import ser460.sundevilconnect.server.events.filter.FilterStrategyFactory;
import ser460.sundevilconnect.shared.proto.EventBrowsingServiceGrpc.*;
import ser460.sundevilconnect.shared.proto.EventBrowsingServiceProto.*;
import ser460.sundevilconnect.shared.proto.EntitiesProto.Event;

import java.util.List;

public class EventBrowsingController extends EventBrowsingServiceImplBase {

    private final EventDAO eventDAO = new EventDAO();

    @Override
    public void getAllEvents(GetAllEventsRequest request,
                             StreamObserver<EventListResponse> responseObserver) {

        var events = eventDAO.getAllEvents();

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

        String category = request.getCategory();

        List<Event> events = eventDAO.getEventsByCategory(category);

        responseObserver.onNext(
                EventListResponse.newBuilder()
                        .addAllEvents(events)
                        .build()
        );

        responseObserver.onCompleted();
    }

    @Override
    public void searchEvents(SearchEventsRequest request,
                             StreamObserver<EventListResponse> responseObserver) {

        String keyword = request.getKeyword();
        List<Event> events = eventDAO.searchEvents(keyword);

        responseObserver.onNext(
                EventListResponse.newBuilder()
                        .addAllEvents(events)
                        .build()
        );

        responseObserver.onCompleted();
    }

    @Override
    public void getEventDetails(GetEventDetailsRequest request,
                                StreamObserver<EventDetailsResponse> responseObserver) {

        int eventId = Integer.parseInt(request.getEventId());

        Event event = eventDAO.getEventById(eventId);

        EventDetailsResponse.Builder responseBuilder = EventDetailsResponse.newBuilder();

        if (event != null) {
            responseBuilder.setEvent(event);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getFilteredEvents(GetFilteredEventsRequest request,
                                  StreamObserver<EventListResponse> responseObserver) {

        // 1. Get all events
        List<Event> events = eventDAO.getAllEvents();

        // 2. Build strategies from request
        List<FilterStrategy> filterStrategies =
                FilterStrategyFactory.createStrategies(request.getFiltersList());

        if (filterStrategies == null) {
            filterStrategies = java.util.Collections.emptyList();
        }

        // 3. Apply each filter sequentially
        for (FilterStrategy strategy : filterStrategies) {
            events = strategy.applyFilter(events);
        }

        // 4. Return filtered result
        responseObserver.onNext(
                EventListResponse.newBuilder()
                        .addAllEvents(events)
                        .build()
        );

        responseObserver.onCompleted();
    }
}
