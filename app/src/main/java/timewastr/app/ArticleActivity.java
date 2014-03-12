package timewastr.app;

import android.OnSwipeTouchListener;
import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Zawu on 2/10/14.
 */
public class ArticleActivity extends Activity {

    TextView tv1;
    TextView articleTitle;
    ScrollView sv1;
    OnSwipeTouchListener onSwipeTouchListener;
    ArrayList<String> articleTitles = new ArrayList<String>();
    ArrayList<String> articles = new ArrayList<String>();
    int totalArticleCount = 1;
    int currentArticle = 1;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        tv1 = (TextView)findViewById(R.id.tv1);
        sv1 = (ScrollView)findViewById(R.id.sv1);
        articleTitle = (TextView)findViewById(R.id.articleTitle);
        onSwipeTouchListener = new OnSwipeTouchListener(sv1.getContext()) {
            /*public void onSwipeTop() {
                Toast.makeText(ArticleActivity.this, "top", Toast.LENGTH_SHORT).show();
            }*/
            public void onSwipeLeft() {
                if(totalArticleCount == currentArticle)//If at the newest article
                {
                    Toast.makeText(ArticleActivity.this, "New Article", Toast.LENGTH_SHORT).show();
                    currentArticle++;
                    totalArticleCount++;
                    getNewArticle();
                }
                else if(currentArticle < totalArticleCount )//If at an previous article
                {
                    Toast.makeText(ArticleActivity.this, "Next Article", Toast.LENGTH_SHORT).show();
                    currentArticle++;
                    getReadArticle();
                }
            }
            public void onSwipeRight() {
                if(currentArticle == 1)//If swipe to first article
                {
                    Toast.makeText(ArticleActivity.this, "First Article", Toast.LENGTH_SHORT).show();
                    getReadArticle();
                }
                else//If swipe back to any article before
                {
                    Toast.makeText(ArticleActivity.this, "Previous Article", Toast.LENGTH_SHORT).show();
                    currentArticle--;
                    getReadArticle();
                }
            }
            /*public void onSwipeBottom() {
                Toast.makeText(ArticleActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }*/
        };

        sv1.setOnTouchListener(onSwipeTouchListener);

        loadDoc();
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
        Intent i = new Intent(ArticleActivity.this, FavoritesActivity.class);
        startActivity(i);
    }

    public void signOut() {
        Intent i = new Intent(ArticleActivity.this, LoginActivity.class);
        startActivity(i);
    }

    public void favorite(View view) {
        //Here we will handle favoriting the current article
        ImageButton star = (ImageButton)findViewById(R.id.favorite);
        //we are removing from favorites
        if (star.getTag() == "starOn") {
            star.setTag("starOff");
            star.setImageResource(getResources().getIdentifier("android:drawable/btn_star", null, getPackageName()));
        //we are adding to favorites
        } else {
            star.setTag("starOn");
            star.setImageResource(getResources().getIdentifier("android:drawable/btn_star_big_on", null, getPackageName()));
        }
    }

    private void loadDoc(){
        getNewArticle();
    }

    private void getNewArticle()
    {
        String titleCall = "titleAPICALL " + totalArticleCount;
        //articleTitle.setText(Html.fromHtml(articleCall));
        articleTitles.add(titleCall);
        articleTitle.setText(titleCall);

        String articleCall = "";

        for(int x=0;x<=100;x++){
            articleCall += "articleAPICALL " + totalArticleCount + " " +String.valueOf(x)+"\n";
        }

        //tv1.setText(Html.fromHtml(articleCall));
        articles.add(articleCall);
        tv1.setText(articleCall);
    }

    private void getReadArticle()
    {
        articleTitle.setText(articleTitles.get(currentArticle-1));
        tv1.setText(articles.get(currentArticle-1));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        onSwipeTouchListener.getGestureDetector().onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
}
