package timewastr.app;

/**
 * Created by Zawu on 2/26/14.
 */

import android.UrlJsonAsyncTask;
import android.app.Activity;
import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.impl.client.BasicResponseHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class RegisterActivity extends Activity{
    private SharedPreferences mPreferences;
    private String mUserEmail;
    private String mUserPassword;
    private String mUserPasswordConfirmation;
    private String response;
    private Context self = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
    }

    public void registerNewAccount(View button) {
        EditText userEmailField = (EditText) findViewById(R.id.userEmail);
        mUserEmail = userEmailField.getText().toString();
        EditText userPasswordField = (EditText) findViewById(R.id.userPassword);
        mUserPassword = userPasswordField.getText().toString();
        EditText userPasswordConfirmationField = (EditText) findViewById(R.id.userPasswordConfirmation);
        mUserPasswordConfirmation = userPasswordConfirmationField.getText().toString();

        if (mUserEmail.length() == 0 || mUserPassword.length() == 0 || mUserPasswordConfirmation.length() == 0) {
            // input fields are empty
            Toast.makeText(this, "Please complete all the fields",Toast.LENGTH_LONG).show();
            return;
        } else {
            if (!isValidEmailAddress(mUserEmail)) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_LONG).show();
            } else if (!mUserPassword.equals(mUserPasswordConfirmation)) {
                // password doesn't match confirmation
                Toast.makeText(this, "Your password doesn't match confirmation, check again", Toast.LENGTH_LONG).show();
                return;
            } else {
                Toast.makeText(self, "Registering...",Toast.LENGTH_LONG).show();
                new MyAsyncTask().execute();
            }
        }
    }

    public boolean isValidEmailAddress(String email) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(".+@.+\\.[a-z]+");
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public void register() throws Exception {
        //Do a get request and grab data
        URL url = new URL("http://timewastr.herokuapp.com/register/" + mUserEmail + "/" + mUserPassword);
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

    public void registerComplete() throws JSONException {
        JSONObject data = new JSONObject(response);
        MyApp app = ((MyApp)getApplicationContext());
        app.setToken(data.getString("token"));
        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(i);
    }

    public class MyAsyncTask extends AsyncTask<String, Integer, Integer>{
        @Override
        // THIS IS THE TASK TO DO
        protected Integer doInBackground(String... params) {
            try {
                register();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return 1;
        }
        // THIS IS WHEN TASK IS COMPLETED
        protected void onPostExecute(Integer result){
            if (result == null) {
                Toast.makeText(self, "An account with that email already exists",Toast.LENGTH_LONG).show();
            } else {
                try {
                    registerComplete();
                } catch (JSONException e) {
                    Toast.makeText(self, "There was a problem, please try again",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
    }
}
