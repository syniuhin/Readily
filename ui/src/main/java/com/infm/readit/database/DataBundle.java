package com.infm.readit.database;

import android.content.Intent;

import com.infm.readit.Constants;

/**
 * Created by infm on 6/24/14. Enjoy ;)
 */
public class DataBundle {
    private String header;
    private String path;
    private Integer position;
    private String percent;

    public DataBundle(String header, String path, Integer position, String percent) {
        this.header = header;
        this.path = path;
        this.position = position;
        this.percent = percent;
    }

    public static DataBundle createFromIntent(Intent intent) {
        return new DataBundle(intent.getStringExtra(Constants.EXTRA_HEADER),
                intent.getStringExtra(Constants.EXTRA_PATH),
                intent.getIntExtra(Constants.EXTRA_POSITION, 0),
                intent.getStringExtra(Constants.EXTRA_PERCENT));
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    @Override
    public String toString() {
        return "header: " + header + "; path: " + path + "; position: " + position + "; percent: " + percent;
    }
}
