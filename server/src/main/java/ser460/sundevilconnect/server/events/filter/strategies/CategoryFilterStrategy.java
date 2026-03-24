package ser460.sundevilconnect.server.events.filter.strategies;

import ser460.sundevilconnect.server.events.Event;
import ser460.sundevilconnect.server.events.filter.FilterStrategy;

import java.util.List;

public class CategoryFilterStrategy implements FilterStrategy {
    private String category;

    public CategoryFilterStrategy(String category) {
        this.category = category;
    }

    @Override
    public List<Event> applyFilter(List<Event> events) {
        return List.of();
    }
}
