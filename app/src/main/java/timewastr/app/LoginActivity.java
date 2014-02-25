package timewastr.app; /**
 * Created by Zawu on 2/10/14.
 */
import android.UrlJsonAsyncTask;
import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.impl.client.BasicResponseHandler;

import java.io.IOException;

public class LoginActivity extends Activity {

    private EditText username=null;
    private EditText password=null;
    private Button login;

    private final static String LOGIN_API_ENDPOINT_URL = "http://0.0.0.0:3000/api/v1/sessions";
    public static SharedPreferences mPreferences;
    public static String mUserEmail;
    public static String mUserPassword;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        //username = (EditText)findViewById(R.id.userEmail);
        //password = (EditText)findViewById(R.id.userPassword);

        //works before
        /*login = (Button)findViewById(R.id.button1);
        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        login.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            }
        });*/

    }

    /*public void login(View view){
        if(username.getText().toString().equals("admin") &&
                password.getText().toString().equals("admin")){
            Toast.makeText(getApplicationContext(), "Redirecting...",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "Wrong Credentials",
                    Toast.LENGTH_SHORT).show();
        }

    }*/

    public void login(View button) {
        EditText userEmailField = (EditText) findViewById(R.id.mUserEmail);
        mUserEmail = userEmailField.getText().toString();
        EditText userPasswordField = (EditText) findViewById(R.id.mUserPassword);
        mUserPassword = userPasswordField.getText().toString();

        if (mUserEmail.length() == 0 || mUserPassword.length() == 0) {
            // input fields are empty
            Toast.makeText(this, "Please complete all the fields",
                    Toast.LENGTH_LONG).show();
            return;
        } else {
            LoginTask loginTask = new LoginTask(LoginActivity.this);
            loginTask.setMessageLoading("Logging in...");
            loginTask.execute(LOGIN_API_ENDPOINT_URL);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // file: LoginActivity.java
    class LoginTask extends UrlJsonAsyncTask {
        public LoginTask(Context context) {
            super(context);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(urls[0]);
            JSONObject holder = new JSONObject();
            JSONObject userObj = new JSONObject();
            String response = null;
            JSONObject json = new JSONObject();

            try {
                try {
                    // setup the returned values in case
                    // something goes wrong
                    json.put("success", false);
                    json.put("info", "Something went wrong. Retry!");
                    // add the user email and password to
                    // the params
//                    String mUserEmail = LoginActivity.mUserEmail;
//                    String mUserPassword = LoginActivity.mUserPassword;
                    userObj.put("email", mUserEmail);
                    userObj.put("password", mUserPassword);
                    holder.put("user", userObj);
                    StringEntity se = new StringEntity(holder.toString());
                    post.setEntity(se);

                    // setup the request headers
                    post.setHeader("Accept", "application/json");
                    post.setHeader("Content-Type", "application/json");

                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    response = client.execute(post, responseHandler);
                    json = new JSONObject(response);

                } catch (HttpResponseException e) {
                    e.printStackTrace();
                    Log.e("ClientProtocol", "" + e);
                    json.put("info", "Email and/or password are invalid. Retry!");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("IO", "" + e);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSON", "" + e);
            }

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json.getBoolean("success")) {
                    // everything is ok
                    SharedPreferences mPreferences = LoginActivity.mPreferences;
                    SharedPreferences.Editor editor = mPreferences.edit();
                    // save the returned auth_token into
                    // the SharedPreferences
                    editor.putString("AuthToken", json.getJSONObject("data").getString("auth_token"));
                    editor.commit();

                    // launch the HomeActivity and close this one
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                Toast.makeText(context, json.getString("info"), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                // something went wrong: show a Toast
                // with the exception message
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                super.onPostExecute(json);
            }
        }
    }


}



