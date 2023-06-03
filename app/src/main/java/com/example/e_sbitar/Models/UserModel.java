package com.example.e_sbitar.Models;

import java.io.Serializable;

public class UserModel implements Serializable {
    private String UserId, Name, Email, Type, ProfilePic;
    private boolean IsAdmin;

    public UserModel(String userId, String name, String email, String type, String profilePic, boolean isAdmin) {
        UserId = userId;
        Name = name;
        Email = email;
        Type = type;
        ProfilePic = profilePic;
        IsAdmin = isAdmin;
    }

    public UserModel() {
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getProfilePic() {
        return ProfilePic;
    }

    public void setProfilePic(String profilePic) {
        ProfilePic = profilePic;
    }

    public boolean getIsAdmin() {
        return IsAdmin;
    }

    public void setAdmin(boolean admin) {
        IsAdmin = admin;
    }
}
