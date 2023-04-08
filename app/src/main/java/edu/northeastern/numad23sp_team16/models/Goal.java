package edu.northeastern.numad23sp_team16.models;


public class Goal {

    private String goalId;
    private String userId;
    private String goalName;
    private int icon;
    private Boolean reminderOn;
    private String reminderMessage;
    private int reminderHour;
    private int reminderMinute;
    private String startDate;
    private String endDate;
    private int priority;
    private String memo;

    // Constructor for when reminder turned on
    public Goal(String user, String goalName, int icon, Boolean reminderOn, String reminderMessage,
                int reminderHour, int reminderMinute, String startDate, String endDate,
                int priority, String memo) {
        this.userId = user;
        this.goalId = createUniqueGoalId();
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
    }

    // Constructor for when reminder turned off
    public Goal(String user, String goalName, int icon, Boolean reminderOn,
                String startDate, String endDate, int priority, String memo) {
        this.userId = user;
        this.goalId = createUniqueGoalId();
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

    // Create unique goal id: goal + time + userId
    private String createUniqueGoalId() {
        String time = String.valueOf(System.currentTimeMillis());
        return ("goal" + time + this.userId);
    }

    public String getGoalId() {
        return goalId;
    }

    public String getUserId() {
        return userId;
    }

    public String getGoalName() {
        return goalName;
    }

    public int getIcon() {
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
}
