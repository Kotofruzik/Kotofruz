package com.example.autoschoolbtgp.ui.users;

public class UserModel {
    private String objectId;
    private String firstName;
    private String lastName;
    private String middleName;
    private String photo;
    private String role;

    public UserModel(String objectId, String firstName, String lastName, String role, String photo) {
        this.objectId = objectId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.role = role;
        this.photo = photo;
    }

    // Getters
    public String getId() { return objectId; }
    public String getName() { return firstName; }
    public String getSurname() { return lastName; }
    public String getMiddleName() { return middleName; }
    public String getRole() { return role; }
    public String getAvatarUrl() { return photo; }

    // Setters
    public void setRole(String role) { this.role = role; }
    public void setMiddleName(String middleName) { this.middleName = middleName;}
}