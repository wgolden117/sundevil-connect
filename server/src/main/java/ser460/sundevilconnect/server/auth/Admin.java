package ser460.sundevilconnect.server.auth;

import ser460.sundevilconnect.server.admin.Content;
import ser460.sundevilconnect.server.clubs.Club;

public class Admin  extends User {
    public void approveClub(Club club) {}
    public void rejectClub(Club club) {}
    public void reviewFlaggedContent(Content content) {}
    public void removeContent(Content content) {}
    public void viewSystemActivity() {}
}
