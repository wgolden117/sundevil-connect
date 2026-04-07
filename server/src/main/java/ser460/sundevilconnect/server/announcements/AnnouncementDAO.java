package ser460.sundevilconnect.server.announcements;

import ser460.sundevilconnect.server.auth.Student;
import ser460.sundevilconnect.server.auth.UserDAO;
import ser460.sundevilconnect.server.clubs.Club;
import ser460.sundevilconnect.server.clubs.ClubDAO;
import ser460.sundevilconnect.server.core.DatabaseService;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class AnnouncementDAO {
    private final DatabaseService db;

    public AnnouncementDAO(DatabaseService db) {
        this.db = db;
    }



    private Announcement mapRow(ResultSet rs) throws SQLException {
        Student createdBy = new Student(
                String.valueOf(rs.getInt("userId")),
                rs.getString("firstName"),
                rs.getString("lastName")
        );

        Club club = new Club();
        club.setClubId(String.valueOf(rs.getInt("clubId")));
        club.setName(rs.getString("clubName"));

        Announcement announcement = new Announcement();
        announcement.setAnnouncementId(String.valueOf(rs.getInt("id")));
        announcement.setTitle(rs.getString("title"));
        announcement.setBody(rs.getString("body"));
        announcement.setPostedDate(LocalDate.parse(rs.getString("postedDate")));
        announcement.setPostedToClub(club);
        announcement.setCreatedBy(createdBy);
        announcement.setStatus(rs.getString("status"));
        return announcement;
    }

    public static EntitiesProto.Announcement toProto(Announcement announcement) {
        EntitiesProto.Announcement.Builder builder = EntitiesProto.Announcement.newBuilder()
                .setAnnouncementId(announcement.getAnnouncementId())
                .setPostedTo(ClubDAO.toProto(announcement.getPostedToClub()))
                .setCreatedBy(UserDAO.toProto(announcement.getCreatedBy()))
                .setStatus(announcement.getStatus());

        if (announcement.getTitle() != null) builder.setTitle(announcement.getTitle());
        if (announcement.getBody() != null) builder.setBody(announcement.getBody());
        if (announcement.getPostedDate() != null) builder.setPostedDate(announcement.getPostedDate().toString());

        return builder.build();
    }
}
