package ser460.sundevilconnect.server.auth;

import ser460.sundevilconnect.server.core.DatabaseService;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

public class UserDAO {
    private final DatabaseService db;

    public UserDAO(DatabaseService db) {
        this.db = db;
    }

    public static EntitiesProto.UserSummary toProto(User user) {
        return EntitiesProto.UserSummary.newBuilder()
                .setUserId(user.getUserId())
                .setDisplayName(user.getFirstName() + " " + user.getLastName())
                .build();
    }
}
