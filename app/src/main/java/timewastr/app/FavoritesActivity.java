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
public class FavoritesActivity extends ListActivity {
    MyApp app;
    String response;
    ArrayList<String> articleTitles = new ArrayList<String>();
    ArrayList<String> articleLinks = new ArrayList<String>();
    String link = "";
    boolean loading = true;
    Context self = this;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = ((MyApp)getApplicationContext());

        new MyAsyncTask().execute();

        setListAdapter(new ArrayAdapter<String>(this, R.layout.activity_favorites, articleTitles));

        ListView listView = getListView();
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, get the link and open it in browser
                link = articleLinks.get((articleTitles).indexOf(((TextView) view).getText().toString()));
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
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
        Intent i = new Intent(FavoritesActivity.this, MainActivity.class);
        startActivity(i);
    }

    public void openFavorites() {
        Intent i = new Intent(FavoritesActivity.this, FavoritesActivity.class);
        startActivity(i);
    }

    public void signOut() {
        SignOut signout = new SignOut(app.getToken());
        signout.execute();
        app.setToken("");
        Intent i = new Intent(FavoritesActivity.this, LoginActivity.class);
        startActivity(i);
    }

    public void query() throws Exception {
        URL url;
        if (loading) {
            url = new URL("http://timewastr.herokuapp.com/favorites/list/" + app.getToken());
        } else {
            url = new URL("http://timewastr.herokuapp.com/favorites/remove/" + app.getToken() + "/?articleLink=" + URLEncoder.encode(link, "UTF-8"));
        }
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
            int dataLength = data.length();
            Iterator<?> keys = data.keys();

            while (keys.hasNext()) {
                String key = (String)keys.next();
                if (data.get(key) instanceof JSONObject) {
                    //Make sure the key is a JSON object, we don't want null values
                    JSONObject nestedData = data.getJSONObject(key);
                    articleTitles.add(nestedData.getString("title"));
                    articleLinks.add(nestedData.getString("articleLink"));
                }
            }
        } else {
            Toast.makeText(FavoritesActivity.this, "Done", Toast.LENGTH_SHORT).show();
        }
    }

    public class MyAsyncTask extends AsyncTask<String, Integer, Integer> {
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
        protected void onPostExecute(Integer result){
            try {
                queryComplete();
            } catch (JSONException e) {
                Toast.makeText(self, "There was a problem, please try again",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            if (loading) {
                loading = false;
            }
        }
    }
}
