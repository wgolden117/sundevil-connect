package ser460.sundevilconnect.server.events.filter.strategies;

import ser460.sundevilconnect.shared.proto.EntitiesProto.Event;
import ser460.sundevilconnect.server.events.filter.FilterStrategy;

import java.util.List;

public class PaidFilterStrategy implements FilterStrategy {
    private final boolean isPaid;

    public PaidFilterStrategy(boolean isPaid) {
        this.isPaid = isPaid;
    }

    @Override
    public List<Event> applyFilter(List<Event> events) {
        return events.stream()
                .filter(event -> event.getIsPaid() == isPaid)
                .toList();
    }
}
