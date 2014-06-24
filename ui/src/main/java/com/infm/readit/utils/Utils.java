package com.infm.readit.utils;

import android.util.Pair;

/**
 * Created by infm on 6/19/14. Enjoy ;)
 */
public abstract class Utils {
    public static final int TYPE_CLIPBOARD = 0;
    public static final int TYPE_TXT = 1;
    public static final int TYPE_EPUB = 2;
    public static final int TYPE_SHARED_LINK = 3;
    public static final int TYPE_SHARED_TEXT = 4;

    protected StringBuilder sb;
    protected int type;
    protected int position;
    protected Pair<Integer, Integer> existingData;
    protected boolean processFailed;

    public Utils() {
        this.sb = new StringBuilder();
        this.type = -1;
        this.position = 0;
        this.processFailed = true;
    }

    public StringBuilder getSb() {
        return sb;
    }

    public void setSb(StringBuilder sb) {
        this.sb = sb;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Pair<Integer, Integer> getExistingData() {
        return existingData;
    }

    public void setExistingData(Pair<Integer, Integer> existingData) {
        this.existingData = existingData;
    }

    public boolean isProcessFailed() {
        return processFailed;
    }

    public void setProcessFailed(boolean processFailed) {
        this.processFailed = processFailed;
    }

    public abstract String getPath();

    public abstract void setPath(String path);

    public abstract void process();

}
