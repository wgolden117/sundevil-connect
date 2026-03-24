package ser460.sundevilconnect.server.admin;

import ser460.sundevilconnect.server.auth.Admin;
import ser460.sundevilconnect.server.clubs.Club;

import java.util.List;

public class ClubApprovalController {
    private List<Club> pendingClubs;

    public void submitClubForApproval(Club club) {}
    public void approveClub(Club club, Admin admin) {}
    public void rejectClub(Club club, Admin admin) {}
    public List<Club> getPendingClubs() { return pendingClubs; }
    public Club reviewClubDetails() { return null; }
}
