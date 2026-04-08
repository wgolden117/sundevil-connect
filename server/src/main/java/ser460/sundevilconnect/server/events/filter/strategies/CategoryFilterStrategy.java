package ser460.sundevilconnect.server.events.filter.strategies;

import ser460.sundevilconnect.shared.proto.EntitiesProto.Event;
import ser460.sundevilconnect.server.events.filter.FilterStrategy;

import java.util.List;

public class CategoryFilterStrategy implements FilterStrategy {
    private final String category;

    public CategoryFilterStrategy(String category) {
        this.category = category;
    }

    @Override
    public List<Event> applyFilter(List<Event> events) {
        return events.stream()
                .filter(event -> event.getCategory().equalsIgnoreCase(category))
                .toList();
    }
}
