package edu.northeastern.numad23sp_team16.models;

public class PetHealth {
    private String owner;
    private float averageHealth;
    private String creationDate;

    public PetHealth(String owner, String creationDate) {
        this.owner = owner;
        this.averageHealth = 100;
        this.creationDate = creationDate;
    }

    public PetHealth(String owner, float averageHealth, String creationDate) {
        this.owner = owner;
        this.averageHealth = averageHealth;
        this.creationDate = creationDate;
    }

    // Default constructor required for calls to DataSnapshot.getValue(PetHealth.class)
    public PetHealth() {
    }

    public String getOwner() {
        return this.owner;
    }

    public float getAverageHealth() {
        return this.averageHealth;
    }

    public String getCreationDate() {
        return this.creationDate;
    }

}
