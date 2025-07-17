package com.example.prm392mnlv.data.models;


public class Feature {
    public static Feature fromJson(String geoJson) {
        return new Feature();
    }

    public Object geometry() {
        return new LineString();
    }
}
