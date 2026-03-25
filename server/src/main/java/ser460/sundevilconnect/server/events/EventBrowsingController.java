package ser460.sundevilconnect.server.events;

import ser460.sundevilconnect.server.events.filter.FilterStrategy;
import ser460.sundevilconnect.shared.proto.EventBrowsingServiceProto.FilterType;

import java.util.List;
import java.util.Map;

public class EventBrowsingController {
    private List<FilterStrategy> filterStrategies;

    public List<Event> getAllEvents() { return null; }
    public List<Event> getEventsByCategory() { return null; }
    public List<Event> applyFilter(Map<FilterType, Object> filters) { return null; }
    public List<Event> searchEvents(String keyword) { return null; }

    // these seem sort of out of place here and seem like they should be in the UI representation
    public Event getEventDetails() { return null; }
    public List<Event> sortEventsByDate() { return null; }
    public List<Event> sortEventsByPopularity() { return null; }
}
