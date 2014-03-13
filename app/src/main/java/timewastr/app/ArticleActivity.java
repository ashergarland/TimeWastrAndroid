package timewastr.app;

import android.OnSwipeTouchListener;
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
    private final String USER_AGENT = "Mozilla/5.0";
    String output = "Please Wait";
    private ProgressDialog pd;
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
//        pd.show(ArticleActivity.this, "Article", "Loading");
        for(int x=0;x<=100;x++){
            articleCall += "articleAPICALL " + totalArticleCount + " " +String.valueOf(x)+"\n";
        }
        new GetArticle().execute(tv1);//Use asynctask to load the article
//        pd.dismiss();
        articles.add(output);
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

    public class GetArticle extends AsyncTask<TextView, Void, String> {
        TextView t;
        String result = "fail";

        @Override
        protected String doInBackground(TextView... params) {
            // TODO Auto-generated method stub
            this.t = params[0];
            return GetSomething();
        }

        final String GetSomething()
        {
            String url = "http://timewastr.herokuapp.com/test";
            BufferedReader inStream = null;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpRequest = new HttpGet(url);
                HttpResponse response = httpClient.execute(httpRequest);
                inStream = new BufferedReader(
                        new InputStreamReader(
                                response.getEntity().getContent()));

                StringBuffer buffer = new StringBuffer("");
                String line = "";
                String NL = System.getProperty("line.separator");
                while ((line = inStream.readLine()) != null) {
                    buffer.append(line + NL);
                }
                inStream.close();

                result = buffer.toString();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }

        //TODO: Read the JSON object here and parse it
        protected void onPostExecute(String page)
        {
            //Logic for JSON parsing - I intend to have x amount of arraylists for each attribute (title, content, published, etc.)
            /*
            JSONObject obj = null;
            try {
                obj = new JSONObject(page);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < obj.length(); ++i) {
                final JSONObject article = obj.
            }
            */
            //this is here to show the json object being passed through - the screen should change in 10-15 seconds
            t.setText(Html.fromHtml(page));

        }
    }

}
