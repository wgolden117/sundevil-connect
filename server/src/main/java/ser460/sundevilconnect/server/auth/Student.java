package ser460.sundevilconnect.server.auth;

import ser460.sundevilconnect.server.clubs.Club;
import ser460.sundevilconnect.server.clubs.ClubMembership;
import ser460.sundevilconnect.server.events.Event;
import ser460.sundevilconnect.server.events.EventRegistration;
import ser460.sundevilconnect.shared.proto.EntitiesProto.Role;

import java.util.List;

public class Student extends User {
    private String studentId;
    private String major;
    private int graduationYear;
    private List<ClubMembership> clubMemberships;
    private List<EventRegistration> registeredEvents;


    public Student(String userId, String email) {
        super(userId, email, Role.STUDENT);
    }

    public List<Event> browseEvents() { return null; }
    public boolean registerForEvent(Event event) { return true; }
    public List<Club> browseClubs() { return null; }
    public void requestClubMembership(Club club) {}
    public void viewClubPage(Club club) {}

    // Getters and Setters

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public int getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(int graduationYear) {
        this.graduationYear = graduationYear;
    }

    public List<ClubMembership> getClubMemberships() {
        return clubMemberships;
    }

    public void setClubMemberships(List<ClubMembership> clubMemberships) {
        this.clubMemberships = clubMemberships;
    }

    public List<EventRegistration> getRegisteredEvents() {
        return registeredEvents;
    }

    public void setRegisteredEvents(List<EventRegistration> registeredEvents) {
        this.registeredEvents = registeredEvents;
    }
}
