package com.example.prm392mnlv.data.dto.response;

import com.google.gson.JsonObject;

import java.util.List;

public class VietmapDirectionResponse {
    public String code;
    public List<Path> paths;

    public static class Path {
        public double distance;
        public double time;
        public JsonObject points; // encoded polyline
    }
}


