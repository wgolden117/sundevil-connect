package ser460.sundevilconnect.server.events;

import ser460.sundevilconnect.server.clubs.Club;

import java.time.LocalDateTime;
import java.util.List;

public class Event {
    private String eventId;
    private String title;
    private String description;
    private LocalDateTime eventDate;
    private String location;
    private String category;
    private Club hostedByClub;
    private int capacity;
    private boolean isPaid;
    private List<EventRegistration> registeredStudents;

    public String getEventDetails() { return ""; }
    public void updateEvent() {}
    public void cancelEvent() {}

    // Getters and Setters

    public List<EventRegistration> getRegisteredStudents() {
        return registeredStudents;
    }

    public void setRegisteredStudents(List<EventRegistration> registeredStudents) {
        this.registeredStudents = registeredStudents;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Club getHostedByClub() {
        return hostedByClub;
    }

    public void setHostedByClub(Club hostedByClub) {
        this.hostedByClub = hostedByClub;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
