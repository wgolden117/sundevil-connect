package ser460.sundevilconnect.server.announcements;

import ser460.sundevilconnect.server.admin.Content;
import ser460.sundevilconnect.server.auth.Student;
import ser460.sundevilconnect.server.clubs.Club;

import java.time.LocalDate;

public class Announcement extends Content {
    private String announcementId;
    private String title;
    private String body;
    private LocalDate postedDate;
    private Club postedToClub;
    private Student createdBy;
    private String status;

    // Getters and Setters

    public String getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(String announcementId) {
        this.announcementId = announcementId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDate getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(LocalDate postedDate) {
        this.postedDate = postedDate;
    }

    public Club getPostedToClub() {
        return postedToClub;
    }

    public void setPostedToClub(Club postedToClub) {
        this.postedToClub = postedToClub;
    }

    public Student getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Student createdBy) {
        this.createdBy = createdBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}