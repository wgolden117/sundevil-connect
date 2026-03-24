package ser460.sundevilconnect.server.clubs;

import ser460.sundevilconnect.server.auth.Student;

import java.time.LocalDateTime;

public class ClubMembership {
    private String membershipId;
    private Student student;
    private Club club;
    private String role;
    private LocalDateTime joinDate;
    private String status;

    public void revoke() {}
    public void promoteToLeader() {}

    // Getters and Setters

    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
