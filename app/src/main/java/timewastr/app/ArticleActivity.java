package timewastr.app;

import android.Article;
import android.OnSwipeTouchListener;
import android.SignOut;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.analytics.tracking.android.EasyTracker;

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

/**
 * Created by Zawu on 2/10/14.
 */
public class ArticleActivity extends Activity {
    Bitmap currentImage;
    ImageView articleImage;
    TextView articleContent;
    TextView articleTitle;
    ScrollView scrollView;
    OnSwipeTouchListener onSwipeTouchListener;
    ArrayList<Article> articles = new ArrayList<Article>();
    int currentArticle = 0;

    @Override
    public void onStart() {
        super.onStart();

        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        setResult(2);
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    protected void onDestroy() {
        setResult(2);
        super.onDestroy();
    }


    boolean adding = false;
    Context self = this;
    String response;
    MyApp app;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Crashlytics.start(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        app = ((MyApp)getApplicationContext());

        Bundle b = this.getIntent().getExtras();
        articles = b.getParcelableArrayList("articles");
//        System.out.println("IN ACTIVITY " + b.getString("test"));
        System.out.println("PRINTING ARTICLE IN ARTICLE ");
        for(Article a: articles)
        {
            System.out.print(a.title + " " + a.time);
        }
        articleContent = (TextView)findViewById(R.id.tv1);
        scrollView = (ScrollView)findViewById(R.id.sv1);
        articleTitle = (TextView)findViewById(R.id.articleTitle);
        onSwipeTouchListener = new OnSwipeTouchListener(scrollView.getContext()) {
            public void onSwipeLeft() {
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
            }
            public void onSwipeRight() {
                if(currentArticle != 0) {
                    Toast.makeText(ArticleActivity.this, "Previous Article", Toast.LENGTH_SHORT).show();
                    currentArticle--;
                    getNewArticle();
                }
            }

        };

        scrollView.setOnTouchListener(onSwipeTouchListener);

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
        String pictureURL = articles.get(currentArticle).picture;
        String link = "View original article at <a href='" + articles.get(currentArticle).link + "'>" + articles.get(currentArticle).link + "</a>";
        String articleContent =  articles.get(currentArticle).content;
        String authorPublish = "By " + author + " on " + publishedOn + "\n\n";
        SpannableStringBuilder ssb = new SpannableStringBuilder(authorPublish + " " + Html.fromHtml(articleContent) + "\n\n" + Html.fromHtml(link));
        if(pictureURL != null)//Handle adding a picture to the textview here
        {
            ssb = new SpannableStringBuilder(authorPublish + " \n\n" + Html.fromHtml(articleContent) + "\n\n" + Html.fromHtml(link));
            System.out.println("TESTING PICTURE URL: " + pictureURL);
            new DownloadImageTask().execute(pictureURL);
            ImageSpan imageSpan = new ImageSpan(this, currentImage);
            ssb.setSpan(imageSpan, authorPublish.length(), authorPublish.length()+1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        }
        this.articleContent.setText(ssb, TextView.BufferType.SPANNABLE);
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {


        public DownloadImageTask() {}

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            currentImage = result;
        }
    }

}
