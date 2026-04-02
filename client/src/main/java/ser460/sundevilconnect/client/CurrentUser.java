package ser460.sundevilconnect.client;

import ser460.sundevilconnect.shared.proto.EntitiesProto;

public class CurrentUser {
    private static CurrentUser instance;

    private String userId;
    private String userName;
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
        this.userName = userName;
        this.role = role;
        this.sessionToken = sessionToken;
    }

    public void logout() {
        this.userId = null;
        this.userName = null;
        this.role = null;
        this.sessionToken = null;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public EntitiesProto.Role getRole() {
        return role;
    }

    public String getSessionToken() {
        return sessionToken;
    }
}
