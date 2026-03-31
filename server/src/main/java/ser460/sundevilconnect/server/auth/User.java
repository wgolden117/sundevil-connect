package ser460.sundevilconnect.server.auth;

import ser460.sundevilconnect.shared.proto.EntitiesProto.Role;

public class User {
    private String userId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Role role;

    public User(String userId, String email, Role role) {
        this.userId = userId;
        this.email = email;
        this.role = role;
    }
    public boolean login() { return true;}
    public void logout() {}
    public void updateProfile() {}
    public void resetPassword() {}

    // Getters and Setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
