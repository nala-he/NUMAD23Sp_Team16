package edu.northeastern.numad23sp_team16.models;

public class Icon {
    private int iconId;
    private String iconName;

    public Icon(int iconId, String iconName) {
        this.iconId = iconId;
        this.iconName = iconName;
    }

    public String getIconName() {
        return iconName;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }
}