package edu.northeastern.numad23sp_team16.models;


public class Goal {

    private String userId;
    private String goalName;
    //name of icon, not id
    private String icon;
    private Boolean reminderOn;
    private String reminderMessage;
    private int reminderHour;
    private int reminderMinute;
    private String startDate;
    private String endDate;
    private int priority;
    private String memo;
    private int isCheckedForToday;
    //if lastCheckedInDate!=currentDate,isCheckedForToday=0
    private String lastCheckedInDate;
    //default constructor for firebase
    public Goal(){}
    // Constructor for when reminder turned on
    public Goal(String user, String goalName, String icon, Boolean reminderOn, String reminderMessage,
                int reminderHour, int reminderMinute, String startDate, String endDate,
                int priority, String memo) {
        this.userId = user;
        this.goalName = goalName;
        this.icon = icon;
        this.reminderOn = reminderOn;
        this.reminderMessage = reminderMessage;
        this.reminderHour = reminderHour;
        this.reminderMinute = reminderMinute;
        this.startDate = startDate;
        this.endDate = endDate;
        this.priority = priority;
        this.memo = memo;
        this.isCheckedForToday = 0;
        this.lastCheckedInDate = "N";
    }

    // Constructor for when reminder turned off
    public Goal(String user, String goalName, String icon, Boolean reminderOn,
                String startDate, String endDate, int priority, String memo) {
        this.userId = user;
        this.goalName = goalName;
        this.icon = icon;
        this.reminderOn = reminderOn;
        this.reminderMessage = null;
        this.reminderHour = -1;
        this.reminderMinute = -1;
        this.startDate = startDate;
        this.endDate = endDate;
        this.priority = priority;
        this.memo = memo;
    }

    public String getUserId() {
        return userId;
    }

    public String getGoalName() {
        return goalName;
    }

    public String getIcon() {
        return icon;
    }
    public Boolean getReminderOn() {
        return reminderOn;
    }

    public String getReminderMessage() {
        return reminderMessage;
    }

    public int getReminderHour() {
        return reminderHour;
    }

    public int getReminderMinute() {
        return reminderMinute;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public int getPriority() {
        return priority;
    }

    public String getMemo() {
        return memo;
    }
    public int getIsCheckedForToday() {
        return isCheckedForToday;
    }

    public String getLastCheckedInDate() {
        return lastCheckedInDate;
    }

}
