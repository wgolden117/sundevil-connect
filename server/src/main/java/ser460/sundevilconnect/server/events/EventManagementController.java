package ser460.sundevilconnect.server.events;

import ser460.sundevilconnect.server.auth.Student;
import ser460.sundevilconnect.server.clubs.Club;

import java.util.List;

public class EventManagementController {
    public void createEvent(Event event, Student clubLeader) {}
    public void updateEvent(Event newEvent, Event oldEvent) {}
    public void cancelEvent(Event event) {}
    public List<Event> getEventsForClub(Club club) { return null; }
}
