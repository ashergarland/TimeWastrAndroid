package android;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import timewastr.app.MainActivity;
import timewastr.app.MyApp;
import timewastr.app.RegisterActivity;

/**
 * Created by Skylar on 3/13/14.
 */
public class SignOut {
    private String token;
    private String response;

    public SignOut (String token) {
        this.token = token;
    }

    public void execute() {
        new MyAsyncTask().execute();
    }

    public void signOut() throws Exception {
        //Do a get request and grab data
        URL url = new URL("http://timewastr.herokuapp.com/logout/" + token);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"), 8);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null)
        {
            sb.append(line + "\n");
        }
        response = sb.toString();
        reader.close();
    }

    public void signOutComplete() throws JSONException {
        JSONObject data = new JSONObject(response);
    }

    public class MyAsyncTask extends AsyncTask<String, Integer, Integer> {
        @Override
        // THIS IS THE TASK TO DO
        protected Integer doInBackground(String... params) {
            try {
                signOut();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return 1;
        }
        // THIS IS WHEN TASK IS COMPLETED
        protected void onPostExecute(Integer result){
            try {
                signOutComplete();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
