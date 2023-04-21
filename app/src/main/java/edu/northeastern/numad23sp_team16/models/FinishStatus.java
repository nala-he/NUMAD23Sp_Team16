package edu.northeastern.numad23sp_team16.models;

import java.util.Map;

public class FinishStatus {
    private String userId;
    private int percentageOfToday;
    private Map<String, Integer> dateMap;

    public FinishStatus() {
        // Default constructor required for calls to DataSnapshot.getValue(FinishStatus.class)
    }

    public FinishStatus(String userId, int percentageOfToday, Map<String, Integer> dateMap) {
        this.userId = userId;
        this.percentageOfToday = percentageOfToday;
        this.dateMap = dateMap;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getPercentageOfToday() {
        return percentageOfToday;
    }

    public void setPercentageOfToday(int percentageOfToday) {
        this.percentageOfToday = percentageOfToday;
    }

    public Map<String, Integer> getDateMap() {
        return dateMap;
    }

    public void setDateMap(Map<String, Integer> dateMap) {
        this.dateMap = dateMap;
    }
}

