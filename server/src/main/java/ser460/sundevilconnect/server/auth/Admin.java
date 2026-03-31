package ser460.sundevilconnect.server.auth;

import ser460.sundevilconnect.server.admin.Content;
import ser460.sundevilconnect.server.clubs.Club;
import ser460.sundevilconnect.shared.proto.EntitiesProto.Role;

public class Admin  extends User {

    public Admin(String userId, String email) {
        super(userId, email, Role.ADMIN);
    }

    public void approveClub(Club club) {}
    public void rejectClub(Club club) {}
    public void reviewFlaggedContent(Content content) {}
    public void removeContent(Content content) {}
    public void viewSystemActivity() {}
}
