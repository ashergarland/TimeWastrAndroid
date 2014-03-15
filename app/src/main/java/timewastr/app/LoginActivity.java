package timewastr.app; /**
 * Created by Zawu on 2/10/14.
 */
import android.ToastSingleton;
import android.UrlJsonAsyncTask;
import android.app.Activity;
import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.impl.client.BasicResponseHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.*;
import java.util.Map;

public class LoginActivity extends Activity {
    private Button login;
    private Button register;
    public static SharedPreferences mPreferences;
    public static String mUserEmail;
    public static String mUserPassword;
    private EasyTracker easyTracker = null;

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }
    String response;
    MyApp app;

    Context self = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       Crashlytics.start(this);

        setContentView(R.layout.activity_login);
        app = ((MyApp)getApplicationContext());

        register = (Button)findViewById(R.id.registerButton);
        register.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        });
    }

    public void loginTriggered(View button) {
        EditText userEmailField = (EditText) findViewById(R.id.mUserEmail);
        mUserEmail = userEmailField.getText().toString();
        EditText userPasswordField = (EditText) findViewById(R.id.userPassword);
        mUserPassword = userPasswordField.getText().toString();

        if (mUserEmail.length() == 0 || mUserPassword.length() == 0) {
            // input fields are empty
            Toast.makeText(this, "Please complete all the fields",
                    Toast.LENGTH_LONG).show();
            return;
        } else {
            Toast.makeText(this, "Logging in...",Toast.LENGTH_LONG).show();
            new MyAsyncTask().execute();
        }
    }

    public void login() throws Exception {
        //Do a get request and grab data
        URL url = new URL("http://timewastr.herokuapp.com/login");
        Map<String,Object> params = new LinkedHashMap();
        params.put("email", mUserEmail);
        params.put("password", mUserPassword);
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String,Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);

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

    public void loginComplete() throws JSONException {
        JSONObject data = new JSONObject(response);
        app.setToken(data.getString("token"));
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
    }

    public class MyAsyncTask extends AsyncTask<String, Integer, Integer> {
        @Override
        // THIS IS THE TASK TO DO
        protected Integer doInBackground(String... params) {
            try {
                login();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return 1;
        }
        // THIS IS WHEN TASK IS COMPLETED
        protected void onPostExecute(Integer result){
            if (result == null) {
                Toast.makeText(self, "Your email or password are wrong",Toast.LENGTH_LONG).show();
            } else {
                try {
                    loginComplete();
                } catch (JSONException e) {
                    Toast.makeText(self, "There was a problem, please try again",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
    }

}



