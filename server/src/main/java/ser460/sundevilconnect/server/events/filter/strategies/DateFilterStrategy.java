package ser460.sundevilconnect.server.events.filter.strategies;

import ser460.sundevilconnect.shared.proto.EntitiesProto.Event;
import ser460.sundevilconnect.server.events.filter.FilterStrategy;

import java.time.LocalDateTime;
import java.util.List;

public class DateFilterStrategy implements FilterStrategy {
    private final LocalDateTime fromDate;
    private final LocalDateTime toDate;

    public DateFilterStrategy(LocalDateTime fromDate, LocalDateTime toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    @Override
    public List<Event> applyFilter(List<Event> events) {
        return events.stream()
                .filter(event -> {
                    LocalDateTime eventDate = LocalDateTime.parse(event.getEventDate());

                    return (eventDate.isAfter(fromDate) || eventDate.isEqual(fromDate)) &&
                            (eventDate.isBefore(toDate) || eventDate.isEqual(toDate));
                })
                .toList();
    }
}
