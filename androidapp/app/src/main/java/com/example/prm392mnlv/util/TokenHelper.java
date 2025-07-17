package com.example.prm392mnlv.util;

import com.auth0.android.jwt.DecodeException;
import com.auth0.android.jwt.JWT;

import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TokenHelper {
    private static JWT jwt;

    public static boolean setToken(String token) {
        try{
            jwt = new JWT(token);
            return true;
        }catch (DecodeException ex){
            return false;
        }
    }

    public static String getUserId() {
        if (jwt == null)
            return null;
        return jwt.getClaim("nameid").asString();
    }

    public static String getEmail() {
        if (jwt == null)
            return null;
        return jwt.getClaim("email").asString();
    }

    public static Date getIssuedAt() {
        try{
            if (jwt == null) return null;
            return jwt.getIssuedAt();
        }catch (Exception ex){
            return null;
        }
    }

    public static Date getExpiresAt() {
        try{
            if (jwt == null) return null;
            return jwt.getExpiresAt();
        }catch (Exception ex){
            return null;
        }
    }

    public static String getRole() {
        if (jwt == null)
            return null;
        return jwt.getClaim("role").asString();
    }
}
