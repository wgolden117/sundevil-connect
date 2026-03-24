package ser460.sundevilconnect.server.clubs;

import ser460.sundevilconnect.server.auth.Student;
import ser460.sundevilconnect.server.auth.User;

import java.util.List;

public class ClubMembershipController {
    public MembershipRequest requestMembership(Student student, Club club) { return null; }
    public void approveMembershipRequest(MembershipRequest request, User approver) {}
    public void rejectMembershipRequest(MembershipRequest request, User approver) {}
    public List<MembershipRequest> getPendingRequests(Club club) { return null; }
    public void removeMembership(ClubMembership membership) {}
    public List<ClubMembership> getMembershipsForStudent(Student student) { return null; }
}
