package ser460.sundevilconnect.server.admin;

import ser460.sundevilconnect.server.auth.User;

import java.util.List;

public class ContentModerationController {
    private List<Content> flaggedContent;

    public void flagContent(Content content, String reason, User user) {}
    public void reviewFlaggedContent(Content content) {}
    public void approveFlaggedContent(Content content) {}
    public void removeContent(Content content) {}
    public List<Content> getFlaggedContent() { return flaggedContent; }
    public void unflagContent(Content content) {}
}
