package ser460.sundevilconnect.server.events;

import ser460.sundevilconnect.server.auth.Student;

import java.util.List;

public class EventRegistrationController {
    public EventRegistration registerStudentForEvent(Student student, Event event) { return null; }
    public void cancelRegistration(EventRegistration registration) {}
    public List<EventRegistration> getRegistrationsForStudent(Student student) { return null; }
    public List<EventRegistration> getRegistrationsForEvent(Event event) { return null; }
    public boolean checkEventCapacity(Event event) { return false; }
}
