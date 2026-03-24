package ser460.sundevilconnect.server.clubs;

import ser460.sundevilconnect.server.auth.Student;
import ser460.sundevilconnect.server.auth.User;

import java.time.LocalDateTime;

public class MembershipRequest {
    private String requestId;
    private Student student;
    private Club club;
    private LocalDateTime requestDate;
    private String status;
    private User reviewedBy;
    private LocalDateTime reviewDate;

    public void approve() {}
    public void reject() {}
    public String getRequestDetails() { return ""; }

    // Getters and Setters

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
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

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(User reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }
}
