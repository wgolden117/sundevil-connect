package ser460.sundevilconnect.server.events;

import ser460.sundevilconnect.server.auth.Student;

import java.time.LocalDateTime;

public class EventRegistration {
    private String registrationId;
    private Student student;
    private Event event;
    private LocalDateTime registrationDate;
    private String status;

    public void changeStatus() {}
    public void cancel() {}

    // Getters and Setters

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
