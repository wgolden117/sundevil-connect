package ser460.sundevilconnect.server.events.filter.strategies;

import ser460.sundevilconnect.shared.proto.EntitiesProto.Event;
import ser460.sundevilconnect.server.events.filter.FilterStrategy;

import java.util.List;

public class ClubFilterStrategy implements FilterStrategy {
    private String club;

    public ClubFilterStrategy(String club) {
        this.club = club;
    }

    @Override
    public List<Event> applyFilter(List<Event> events) {
        return List.of();
    }
}
