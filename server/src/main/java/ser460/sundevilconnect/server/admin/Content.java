package ser460.sundevilconnect.server.admin;

import ser460.sundevilconnect.server.auth.User;

import java.time.LocalDateTime;

public class Content {
    private String contentId;
    private User createdBy;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;
    private String status;
    private boolean isFlagged;
    private String flagReason;

    public void flag() {}
    public boolean isFlagged() {return isFlagged; }
    public void remove() {}

    // Getters and Setters

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFlagged(boolean flagged) {
        isFlagged = flagged;
    }

    public String getFlagReason() {
        return flagReason;
    }

    public void setFlagReason(String flagReason) {
        this.flagReason = flagReason;
    }
}
