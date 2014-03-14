package timewastr.app;

import android.app.Application;

/**
 * Created by Skylar on 3/13/14.
 */
public class MyApp extends Application {
    private String token = "";
    public String getToken() {
        return this.token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}