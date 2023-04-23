package edu.northeastern.numad23sp_team16.Project;

import android.os.Parcel;
import android.os.Parcelable;

public class Username implements Parcelable {
    private String username;
    private boolean isSelected = false;
    private String userId;

//    public Username(String username) {
//        this.username = username;
//    }

    public Username(String username, String userId) {
        this.username = username;
        this.userId = userId;
    }


    private Username(Parcel in) {
        this.username = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
    }

    public static final Parcelable.Creator<Username> CREATOR = new Parcelable.Creator<Username>() {
        public Username createFromParcel(Parcel in) {
            return new Username(in);
        }

        public Username[] newArray(int size) {
            return new Username[size];

        }
    };

    public String getName() {
        return this.username;
    }
    public boolean isSelected() {
        return isSelected;
    }
    public String getUserId() { return this.userId; }
    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }
}
