package com.example.doctorave.ModelClasses;

import java.util.List;

public class User {

    String userId, name, email, profilePic;
    List<String> patientIds;

    public User() {
        // Default Constructor mandatory for Firebase
    }

    public User(String userId, String name, String email, String profilePic,
                List<String> patientIds) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.profilePic = profilePic;
        this.patientIds = patientIds;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public List<String> getPatientIds() {
        return patientIds;
    }

    public void setPatientIds(List<String> patientIds) {
        this.patientIds = patientIds;
    }
}
