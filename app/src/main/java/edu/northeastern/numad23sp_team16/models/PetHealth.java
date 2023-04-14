package edu.northeastern.numad23sp_team16.models;

public class PetHealth {
    private String owner;
    private float averageHealth;
    private int totalDays;
    private float totalHealth;

    public PetHealth(String owner) {
        this.owner = owner;
        this.averageHealth = 100;
        this.totalDays = 0;
        this.totalHealth = 0;
    }

    public PetHealth(String owner, float averageHealth, int totalDays, float totalHealth) {
        this.owner = owner;
        this.averageHealth = averageHealth;
        this.totalDays = totalDays;
        this.totalHealth = totalHealth;
    }

    public String getOwner() {
        return this.owner;
    }

    public float getAverageHealth() {
        return this.averageHealth;
    }

    public int getTotalDays() {
        return this.totalDays;
    }

    public float getTotalHealth() {
        return this.totalHealth;
    }
}
