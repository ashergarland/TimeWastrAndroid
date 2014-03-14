package timewastr.app;

import android.Article;
import android.DataWrapper;
import android.OnSwipeTouchListener;
import android.SignOut;
import android.app.*;
import android.content.Context;
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
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

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
    boolean adding = false;
    Context self = this;
    String response;
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
                    if (currentArticle == articles.size() - 1) {
                        Intent i = new Intent(ArticleActivity.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(i);
                    } else {
                        Toast.makeText(ArticleActivity.this, "Next Article", Toast.LENGTH_SHORT).show();
                        currentArticle++;
                        getNewArticle();
                    }
//                }
            }
            public void onSwipeRight() {
                if(currentArticle != 0) {
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
            Toast.makeText(ArticleActivity.this, "Removing from favorites...", Toast.LENGTH_SHORT).show();
            articles.get(currentArticle).favorite = false;
            star.setTag("starOff");
            star.setImageResource(getResources().getIdentifier("android:drawable/btn_star", null, getPackageName()));
            new MyAsyncTask().execute();
        //we are adding to favorites
        } else {
            Toast.makeText(ArticleActivity.this, "Adding to favorites...", Toast.LENGTH_SHORT).show();
            articles.get(currentArticle).favorite = true;
            adding = true;
            star.setTag("starOn");
            star.setImageResource(getResources().getIdentifier("android:drawable/btn_star_big_on", null, getPackageName()));
            new MyAsyncTask().execute();
        }
    }

    public void fav() throws Exception {
        URL url;
        if (adding) {
            url = new URL("http://timewastr.herokuapp.com/favorites/add/" + app.getToken() + "/?title=" + URLEncoder.encode(articles.get(currentArticle).title, "UTF-8") + "&articleLink=" + URLEncoder.encode(articles.get(currentArticle).link, "UTF-8"));
        } else {
            url = new URL("http://timewastr.herokuapp.com/favorites/remove/" + app.getToken() + "/?articleLink=" + URLEncoder.encode(articles.get(currentArticle).link, "UTF-8"));
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

    public void favComplete() throws JSONException {
        JSONObject data = new JSONObject(response);
        Toast.makeText(ArticleActivity.this, "Done", Toast.LENGTH_SHORT).show();
    }

    public class MyAsyncTask extends AsyncTask<String, Integer, Integer>{
        @Override
        // THIS IS THE TASK TO DO
        protected Integer doInBackground(String... params) {
            try {
                fav();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return 1;
        }
        // THIS IS WHEN TASK IS COMPLETED
        protected void onPostExecute(Integer result){
            try {
                favComplete();
            } catch (JSONException e) {
                Toast.makeText(self, "There was a problem, please try again",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            adding = false;
        }
    }

    private void loadDoc(){
        getNewArticle();
    }

    private void getNewArticle()
    {
        String titleCall = articles.get(currentArticle).title;
        //articleTitle.setText(Html.fromHtml(articleCall));

        articleTitle.setText(titleCall + " (" + (currentArticle + 1) + " of " + articles.size() + ")");
        String author = articles.get(currentArticle).author;
        if(author.length() == 0)
        {
            author = "Unknown";
        }
        String publishedOn = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("EEEE, MMMM dd, yyyy HH:mm a");
        try {
            Date date = formatter.parse(articles.get(currentArticle).published);
            publishedOn = output.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String pictures = articles.get(currentArticle).picture;
        String link = "View original article at <a href='" + articles.get(currentArticle).link + "'>" + articles.get(currentArticle).link + "</a>";
        String articleContent =  articles.get(currentArticle).content;
        String authorPublish = "By " + author + " on " + publishedOn;
        tv1.setText(authorPublish + "\n\n" + Html.fromHtml(articleContent) + "\n\n" + Html.fromHtml(link));
        sv1.postDelayed(new Runnable() {
            @Override
            public void run() {
                sv1.fullScroll(ScrollView.FOCUS_UP);
            }
        }, 600);

        ImageButton star = (ImageButton)findViewById(R.id.favorite);
        if (star.getTag() == "starOn" && articles.get(currentArticle).favorite == false) {
            star.setTag("starOff");
            star.setImageResource(getResources().getIdentifier("android:drawable/btn_star", null, getPackageName()));
        } else if (star.getTag() == "starOff" && articles.get(currentArticle).favorite) {
            star.setTag("starOn");
            star.setImageResource(getResources().getIdentifier("android:drawable/btn_star_big_on", null, getPackageName()));
        }
    }


    //Disables back button on certain pages
    public void onBackPressed() {
        System.out.println("BACK PRESSED Article PAGE");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        onSwipeTouchListener.getGestureDetector().onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

}
