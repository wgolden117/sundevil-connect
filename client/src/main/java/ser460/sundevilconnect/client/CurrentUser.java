package ser460.sundevilconnect.client;

import ser460.sundevilconnect.shared.proto.EntitiesProto;

public class CurrentUser {
    private static CurrentUser instance;

    private String userId;
    private String displayName;
    private EntitiesProto.Role role;
    private String sessionToken;

    private CurrentUser() {}

    public static CurrentUser getInstance() {
        if (instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }

    public void login(String userId, String userName, EntitiesProto.Role role, String sessionToken) {
        this.userId = userId;
        this.displayName = userName;
        this.role = role;
        this.sessionToken = sessionToken;
    }

    public void logout() {
        this.userId = null;
        this.displayName = null;
        this.role = null;
        this.sessionToken = null;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public EntitiesProto.Role getRole() {
        return role;
    }

    public String getSessionToken() {
        return sessionToken;
    }
}
