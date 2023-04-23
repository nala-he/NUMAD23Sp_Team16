package edu.northeastern.numad23sp_team16.A8;

public class Sticker {
    private int stickerId;

    // added sticker count field
    private int stickerCount = 0;

    public Sticker(int stickerId) {
        this.stickerId = stickerId;
    }

    public Sticker(int stickerId, int stickerCount) {
        this.stickerId = stickerId;
        this.stickerCount = stickerCount;
    }


    public int getStickerId() {
        return stickerId;
    }

    public int getStickerCount() {
        return this.stickerCount;
    }

    public void setStickerId(int stickerId) {
        this.stickerId = stickerId;
    }

    public void setStickerCount(int count) {
        this.stickerCount = count;
    }
}
