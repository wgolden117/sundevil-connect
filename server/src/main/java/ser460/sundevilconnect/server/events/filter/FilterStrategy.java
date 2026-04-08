package ser460.sundevilconnect.server.events.filter;

import ser460.sundevilconnect.shared.proto.EntitiesProto.Event;
import java.util.List;

public interface FilterStrategy {
    List<Event> applyFilter(List<Event> events);
}