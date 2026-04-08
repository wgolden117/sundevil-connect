package ser460.sundevilconnect.server.events.filter.strategies;

import ser460.sundevilconnect.shared.proto.EntitiesProto.Event;
import ser460.sundevilconnect.server.events.filter.FilterStrategy;

import java.util.List;

public class PaidFilterStrategy implements FilterStrategy {
    private boolean isPaid;

    public PaidFilterStrategy(boolean isPaid) {
        this.isPaid = isPaid;
    }

    @Override
    public List<Event> applyFilter(List<Event> events) {
        return List.of();
    }
}
