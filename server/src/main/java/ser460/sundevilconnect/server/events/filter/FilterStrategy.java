package ser460.sundevilconnect.server.events.filter;

import ser460.sundevilconnect.server.events.Event;

import java.util.List;

public interface FilterStrategy {
    public List<Event> applyFilter(List<Event> events);
}
