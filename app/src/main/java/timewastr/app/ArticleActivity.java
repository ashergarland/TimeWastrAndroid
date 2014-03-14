package timewastr.app;

import android.Article;
import android.DataWrapper;
import android.OnSwipeTouchListener;
import android.SignOut;
import android.app.*;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Zawu on 2/10/14.
 */
public class ArticleActivity extends Activity {

    TextView tv1;
    TextView articleTitle;
    ScrollView sv1;
    OnSwipeTouchListener onSwipeTouchListener;
    ArrayList<Article> articles = new ArrayList<Article>();
    int currentArticle = 0;
    MyApp app;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        app = ((MyApp)getApplicationContext());

//        DataWrapper dw = (DataWrapper) getIntent().getSerializableExtra("data");
//        articles = dw.getArticles();
//
//        System.out.println("PRINTING ARTICLE IN ARTICLE ");
//        for(Article a: articles)
//        {
//            System.out.print(a.title + " " + a.time);
//        }
        Bundle b = this.getIntent().getExtras();
        articles = b.getParcelableArrayList("articles");
//        System.out.println("IN ACTIVITY " + b.getString("test"));
        System.out.println("PRINTING ARTICLE IN ARTICLE ");
        for(Article a: articles)
        {
            System.out.print(a.title + " " + a.time);
        }

        tv1 = (TextView)findViewById(R.id.tv1);
        sv1 = (ScrollView)findViewById(R.id.sv1);
        articleTitle = (TextView)findViewById(R.id.articleTitle);
        onSwipeTouchListener = new OnSwipeTouchListener(sv1.getContext()) {
            /*public void onSwipeTop() {
                Toast.makeText(ArticleActivity.this, "top", Toast.LENGTH_SHORT).show();
            }*/
            public void onSwipeLeft() {
                /*if(totalArticleCount == currentArticle)//If at the newest article
                {
                    Toast.makeText(ArticleActivity.this, "New Article", Toast.LENGTH_SHORT).show();
                    currentArticle++;
                    totalArticleCount++;
                    getNewArticle();
                }
                else if(currentArticle < totalArticleCount )//If at an previous article
                {*/
                    Toast.makeText(ArticleActivity.this, "Next Article", Toast.LENGTH_SHORT).show();
                    currentArticle++;
                    getNewArticle();
//                }
            }
            public void onSwipeRight() {
                if(currentArticle == 0)//If swipe to first article
                {
                    Toast.makeText(ArticleActivity.this, "First Article", Toast.LENGTH_SHORT).show();
                    getNewArticle();
                }
                else//If swipe back to any article before
                {
                    Toast.makeText(ArticleActivity.this, "Previous Article", Toast.LENGTH_SHORT).show();
                    currentArticle--;
                    getNewArticle();
                }
            }

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
        Intent i = new Intent(ArticleActivity.this, MainActivity.class);
        startActivity(i);
    }

    public void openFavorites() {
        Intent i = new Intent(ArticleActivity.this, FavoritesActivity.class);
        startActivity(i);
    }

    public void signOut() {
        SignOut signout = new SignOut(app.getToken());
        signout.execute();
        app.setToken("");
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
        String titleCall = articles.get(currentArticle).title;
        //articleTitle.setText(Html.fromHtml(articleCall));

        articleTitle.setText(titleCall);
        String author = articles.get(currentArticle).author;
        if(author.length() == 0)
        {
            author = "Unknown";
        }
        String publishedOn = articles.get(currentArticle).published;
        String pictures = articles.get(currentArticle).picture;
        String link = "View original article at " + articles.get(currentArticle).link;
        String articleContent = articles.get(currentArticle).content;
        String authorPublish = "by " + author + " on " + publishedOn;
        tv1.setText(authorPublish + "\n" + Html.fromHtml(articleContent) + "\n\n" + link);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        onSwipeTouchListener.getGestureDetector().onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

}
