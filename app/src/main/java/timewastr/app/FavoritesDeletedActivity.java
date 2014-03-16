package timewastr.app;

import android.OnSwipeTouchListener;
import android.SignOut;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.util.*;

import com.crashlytics.android.Crashlytics;
import com.google.analytics.tracking.android.EasyTracker;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by Skylar on 3/10/14.
 */
public class FavoritesDeletedActivity extends ListActivity {
    MyApp app;
    String response;
    ArrayList<String> articleTitles = new ArrayList<String>();
    ArrayList<String> articleLinks = new ArrayList<String>();
    String link = "";
    boolean loading = true;
    Context self = this;

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
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Crashlytics.start(this);

        super.onCreate(savedInstanceState);

        app = ((MyApp)getApplicationContext());
        Bundle b = this.getIntent().getExtras();
        link = b.getString("deletedLink");
        System.out.println(link);

        new MyAsyncTask().execute();

        System.out.println("PRINTING AFTER ASYNC");
        System.out.println(articleLinks);
        System.out.println(articleTitles);
        setListAdapter(new ArrayAdapter<String>(this, R.layout.activity_favorites, articleTitles));

        ListView listView = getListView();
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, get the link and open it in browser
                link = articleLinks.get((articleTitles).indexOf(((TextView) view).getText().toString()));
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(browserIntent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_home:
                this.openHome();
                return true;
            case R.id.action_favorites:
                this.openFavorites();
                return true;
            case R.id.action_signOut:
                this.signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openHome() {
        Intent i = new Intent(FavoritesDeletedActivity.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
    }

    public void openFavorites() {
        Intent i = new Intent(FavoritesDeletedActivity.this, FavoritesActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
    }

    public void signOut() {
        SignOut signout = new SignOut(app.getToken());
        signout.execute();
        app.setToken("");
        Intent i = new Intent(FavoritesDeletedActivity.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
    }

    public void query() throws Exception {
        URL url;
        url = new URL("http://timewastr.herokuapp.com/favorites/remove/" + app.getToken() + "/?articleLink=" + URLEncoder.encode(link, "UTF-8"));
        //Do a get request and grab data
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
        System.out.println(response);
        reader.close();
    }

    public void queryComplete() throws JSONException {
        JSONObject data = new JSONObject(response);
        if (loading) {
            System.out.println(response);
            Iterator<?> keys = data.keys();

            while (keys.hasNext()) {
                String key = (String)keys.next();
                if (data.get(key) instanceof JSONObject) {
                    //Make sure the key is a JSON object, we don't want null values
                    JSONObject nestedData = data.getJSONObject(key);
                    System.out.println("ADDING TITLE: " + nestedData.getString("title"));
                    System.out.println("ADDING URL: " + nestedData.getString("articleLink"));
                    articleTitles.add(nestedData.getString("title"));
                    articleLinks.add(nestedData.getString("articleLink"));
                }
            }
            System.out.println("Exiting While loop");
        } else {
            Toast.makeText(FavoritesDeletedActivity.this, "Done", Toast.LENGTH_SHORT).show();
        }
    }

    public class MyAsyncTask extends AsyncTask<String, Void, Integer> {
        @Override
        // THIS IS THE TASK TO DO
        protected Integer doInBackground(String... params) {
            try {
                query();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return 1;
        }
        // THIS IS WHEN TASK IS COMPLETED
        @Override
        protected void onPostExecute(Integer result){
            Intent i = new Intent(FavoritesDeletedActivity.this, FavoritesActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);

        }
    }
}
