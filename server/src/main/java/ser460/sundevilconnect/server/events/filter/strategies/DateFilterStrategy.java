package ser460.sundevilconnect.server.events.filter.strategies;

import ser460.sundevilconnect.shared.proto.EntitiesProto.Event;
import ser460.sundevilconnect.server.events.filter.FilterStrategy;

import java.time.LocalDateTime;
import java.util.List;

public class DateFilterStrategy implements FilterStrategy {
    private LocalDateTime fromDate;
    private LocalDateTime toDate;

    public DateFilterStrategy(LocalDateTime fromDate, LocalDateTime toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    @Override
    public List<Event> applyFilter(List<Event> events) {
        return List.of();
    }
}
