package me.t3sl4.util.version.model;

import java.util.List;

public class ReleaseDetail {
    private String title;
    private String description;
    private List<String> assets;

    public ReleaseDetail(String title, String description, List<String> assets) {
        this.title = title;
        this.description = description;
        this.assets = assets;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getAssets() {
        return assets;
    }

    @Override
    public String toString() {
        return "Title: " + title + "\nDescription: " + description + "\nAssets: " + assets;
    }
}