package dev.hideftbanana.netcafejavafxapp;

import java.util.prefs.Preferences;

public class TokenManager {
    private static final String ACCESS_TOKEN_KEY = "accessToken";
    private static final String REFRESH_TOKEN_KEY = "refreshToken";

    private static Preferences preferences = Preferences.userNodeForPackage(TokenManager.class);

    public static String getAccessToken() {
        return preferences.get(ACCESS_TOKEN_KEY, null);
    }

    public static void setAccessToken(String accessToken) {
        preferences.put(ACCESS_TOKEN_KEY, accessToken);
    }

    public static String getRefreshToken() {
        return preferences.get(REFRESH_TOKEN_KEY, null);
    }

    public static void setRefreshToken(String refreshToken) {
        preferences.put(REFRESH_TOKEN_KEY, refreshToken);
    }
}
