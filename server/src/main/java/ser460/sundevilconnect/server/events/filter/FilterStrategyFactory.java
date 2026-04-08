package ser460.sundevilconnect.server.events.filter;

import ser460.sundevilconnect.shared.proto.EventBrowsingServiceProto.*;
import ser460.sundevilconnect.server.events.filter.strategies.*;

import java.util.ArrayList;
import java.util.List;

public class FilterStrategyFactory {

    public static List<FilterStrategy> createStrategies(List<EventFilter> filters) {

        List<FilterStrategy> strategies = new ArrayList<>();

        for (EventFilter filter : filters) {

            switch (filter.getType()) {

                case CATEGORY:
                    strategies.add(
                            new CategoryFilterStrategy(
                                    filter.getCategory().getCategory()
                            )
                    );
                    break;
                case DATE:
                    strategies.add(
                            new DateFilterStrategy(
                                    java.time.LocalDateTime.parse(filter.getDate().getFromDate()),
                                    java.time.LocalDateTime.parse(filter.getDate().getToDate())
                            )
                    );
                    break;
                case PAID:
                    strategies.add(
                            new PaidFilterStrategy(
                                    filter.getPaid().getIsPaid()
                            )
                    );
                    break;
                case CLUB:
                    strategies.add(
                            new ClubFilterStrategy(
                                    filter.getClub().getClubId()
                            )
                    );
                    break;
                default:
                    break;
            }
        }
        return strategies;
    }
}