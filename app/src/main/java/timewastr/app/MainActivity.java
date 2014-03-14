package timewastr.app;

import android.SignOut;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.SeekBar;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.analytics.tracking.android.EasyTracker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity {
    MyApp app;
    SeekBar  timeBar;
    TextView clockText;
    Button   goButton;
    TextView minutes;
    Vector<ToggleButton> settings = new Vector<ToggleButton>();

    int time;
    boolean news;
    boolean health;
    boolean finance;
    boolean politics;
    boolean tech;

    private Button go;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = ((MyApp)getApplicationContext());

        timeBar     = (SeekBar)findViewById(R.id.timeBar);
        clockText   = (TextView)findViewById(R.id.clockText);
        minutes     = (TextView)findViewById(R.id.h3);
        settings.add((ToggleButton)findViewById(R.id.news));
        settings.add((ToggleButton)findViewById(R.id.health));
        settings.add((ToggleButton)findViewById(R.id.finance));
        settings.add((ToggleButton)findViewById(R.id.politics));
        settings.add((ToggleButton)findViewById(R.id.tech));

        // Default settings
        timeBar.setMax(60);
        timeBar.setProgress(5);
        clockText.setText(timeBar.getProgress() + "");
        for ( ToggleButton btn : settings) {
            btn.setChecked(true);
        }

        // Initialize timeBar onChangeListener
        timeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {

            public void onStopTrackingTouch(SeekBar bar)
            {

            }

            public void onStartTrackingTouch(SeekBar bar)
            {

            }

            public void onProgressChanged(SeekBar bar, int paramInt, boolean paramBoolean)
            {
                // Minimum value is 1
                if (bar.getProgress() <= 1) {
                    bar.setProgress(1);
                    minutes.setText("minute");
                }
                else {
                    minutes.setText("minutes");
                }
                // Set clock to display the time bar value.
                time = bar.getProgress();
                clockText.setText(time + "");
            }
        });
        go = (Button)findViewById(R.id.goButton);
        go.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){
                news = ((ToggleButton)findViewById(R.id.news)).isChecked();
                health = ((ToggleButton)findViewById(R.id.health)).isChecked();
                finance = ((ToggleButton)findViewById(R.id.finance)).isChecked();
                politics = ((ToggleButton)findViewById(R.id.politics)).isChecked();
                tech = ((ToggleButton)findViewById(R.id.tech)).isChecked();

                Intent i = new Intent(MainActivity.this, LoadingActivity.class);

                i.putExtra("time", time);
                i.putExtra("news", news);
                i.putExtra("health", health);
                i.putExtra("finance", finance);
                i.putExtra("politics", politics);
                i.putExtra("tech", tech);

                startActivity(i);
            }
        });
/*        goButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Package settings
                // int time
                // Vector<ToggleButton> settings
            }
        });*/
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
        Intent i = new Intent(MainActivity.this, MainActivity.class);
        startActivity(i);
    }

    public void openFavorites() {
        Intent i = new Intent(MainActivity.this, FavoritesActivity.class);
        startActivity(i);
    }

    public void signOut() {
        SignOut signout = new SignOut(app.getToken());
        signout.execute();
        app.setToken("");
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_main, container, false);
            return rootView;
        }
    }
}
