package ser460.sundevilconnect.server.announcements;

import ser460.sundevilconnect.server.auth.User;
import ser460.sundevilconnect.server.clubs.Club;

import java.util.List;

public class AnnouncementController {
    public void createAnnouncement(Announcement announcement, User clubLeader) {}
    public void editAnnouncement(Announcement announcement, Announcement updatedAnnouncement) {}
    public void deleteAnnouncement(Announcement announcement) {}
    public List<Announcement> getAnnouncementsForClub(Club club) { return null; }
    public void publishAnnouncement(Announcement announcement) {}
}
