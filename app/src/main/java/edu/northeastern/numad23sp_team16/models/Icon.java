package edu.northeastern.numad23sp_team16.models;

public class Icon {
    private int iconId;
    private String iconName;
    private boolean selected = false;

    public Icon(int iconId, String iconName) {
        this.iconId = iconId;
        this.iconName = iconName;
    }

    public Icon(int iconId, String iconName, boolean isSelected) {
        this.iconId = iconId;
        this.iconName = iconName;
        this.selected = isSelected;
    }

    public String getIconName() {
        return iconName;
    }

    public int getIconId() {
        return iconId;
    }

    public boolean getSelected() {
        return this.selected;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public void setSelected(boolean isSelected) {
        this.selected = isSelected;
    }
}