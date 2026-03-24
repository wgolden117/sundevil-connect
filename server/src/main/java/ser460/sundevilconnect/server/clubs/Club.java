package ser460.sundevilconnect.server.clubs;

import ser460.sundevilconnect.server.announcements.Announcement;
import ser460.sundevilconnect.server.auth.Student;
import ser460.sundevilconnect.server.events.Event;

import java.time.LocalDate;
import java.util.List;

public class Club {
    private String clubId;
    private String name;
    private String description;
    private String category;
    private LocalDate foundedDate;
    private String status;
    private List<ClubMembership> members;
    private List<Event> events;
    private List<Announcement> announcements;

    public void addMember(Student student) {}
    public void removeMember(Student student) {}
    public void createEvent(Event event) {}
    public void postAnnouncement(Announcement announcement) {}
    public List<ClubMembership> getActiveMembers() { return members; }

    // Getters and Setters

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getFoundedDate() {
        return foundedDate;
    }

    public void setFoundedDate(LocalDate foundedDate) {
        this.foundedDate = foundedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ClubMembership> getMembers() {
        return members;
    }

    public void setMembers(List<ClubMembership> members) {
        this.members = members;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(List<Announcement> announcements) {
        this.announcements = announcements;
    }
}
