package com.example.doctorave.Utils;

import java.util.List;

public class ImageGridModel {

    String date;
    List<String> images;

    public ImageGridModel(String date, List<String> images) {
        this.date = date;
        this.images = images;
    }

    public String getDate() {
        return date;
    }

    public List<String> getImages() {
        return images;
    }
}
