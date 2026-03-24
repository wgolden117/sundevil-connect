package ser460.sundevilconnect.server.announcements;

import ser460.sundevilconnect.server.clubs.Club;

public class Announcement {
    private String announcementId;
    private String title;
    private String body;
    private Club postedToClub;
    private String status;

    public void publish() {}
    public void edit() {}

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

    public Club getPostedToClub() {
        return postedToClub;
    }

    public void setPostedToClub(Club postedToClub) {
        this.postedToClub = postedToClub;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
