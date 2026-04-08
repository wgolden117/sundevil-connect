package ser460.sundevilconnect.server.events.filter.strategies;

import ser460.sundevilconnect.shared.proto.EntitiesProto.Event;
import ser460.sundevilconnect.server.events.filter.FilterStrategy;

import java.util.List;

public class ClubFilterStrategy implements FilterStrategy {
    private final String clubId;

    public ClubFilterStrategy(String clubId) {
        this.clubId = clubId;
    }

    @Override
    public List<Event> applyFilter(List<Event> events) {
        return events.stream()
                .filter(event ->
                        event.hasHostedBy() &&
                                event.getHostedBy().getClubId().equals(clubId)
                )
                .toList();
    }
}
