package timewastr.app;

import android.OnSwipeTouchListener;
import android.app.*;
import android.content.Intent;
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

import java.util.Arrays;

/**
 * Created by Skylar on 3/10/14.
 */
public class FavoritesActivity extends ListActivity {
    String[] articleTitles;
    String[] articleLinks;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_article);

        this.getFavorites();
        setListAdapter(new ArrayAdapter<String>(this, R.layout.activity_favorites, articleTitles));

        ListView listView = getListView();
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, get the link (and article?) and send it to the article page
                String link = articleLinks[Arrays.asList(articleTitles).indexOf(((TextView) view).getText().toString())];
                Intent i = new Intent(FavoritesActivity.this, ArticleActivity.class);
                startActivity(i);
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
            case R.id.action_settings:
                //openSettings();
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

    public void openFavorites() {
        Intent i = new Intent(FavoritesActivity.this, FavoritesActivity.class);
        startActivity(i);
    }

    public void signOut() {
        Intent i = new Intent(FavoritesActivity.this, LoginActivity.class);
        startActivity(i);
    }

    public void getFavorites() {
        //Will need to get real articles here when favorites is done
        articleTitles = new String[] { "Apple", "Avocado", "Banana",
                "Blueberry", "Coconut", "Durian", "Guava", "Kiwifruit",
                "Jackfruit", "Mango", "Olive", "Pear", "Sugar-apple" };
        articleLinks = new String[] { "AppleLink", "AvocadoLink", "BananaLink",
                "BlueberryLink", "CoconutLink", "DurianLink", "GuavaLink", "KiwifruitLink",
                "JackfruitLink", "MangoLink", "OliveLink", "PearLink", "Sugar-appleLink" };
    }
}
