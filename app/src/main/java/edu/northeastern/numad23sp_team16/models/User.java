package edu.northeastern.numad23sp_team16.models;
public class User {
    private String email;
    private String username;
    private String password;
    private String petType;
    private String petName;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String username, String password, String petType, String petName) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.petType = petType;
        this.petName = petName;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPetType() {
        return petType;
    }

    public String getPetName() {
        return petName;
    }

    // added setters for profile updates-- Yutong
    public void setUsername(String newUsername) { username = newUsername; }

    public void setEmail(String newEmail) {
        email = newEmail;
    }

    public void setPassword(String newPassword) { password = newPassword; }

    public void setPetType(String newPetType) { petType = newPetType; }

    public void setPetName(String newPetName) { petName = newPetName; }


}
