package timewastr.app;

import android.Article;
import android.DataWrapper;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class LoadingActivity extends ActionBarActivity {
    int time;
    boolean news;
    boolean health;
    boolean finance;
    boolean politics;
    boolean tech;
    String response;
    //HttpResponse response;
    ArrayList<Article> articles = new ArrayList<Article>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Bundle bundle = getIntent().getExtras();
        time = bundle.getInt("time");
        news = bundle.getBoolean("news");
        health = bundle.getBoolean("health");
        finance = bundle.getBoolean("finance");
        politics = bundle.getBoolean("politics");
        tech = bundle.getBoolean("tech");

        ProgressDialog progress = new ProgressDialog(this, 0);
        progress.setTitle("Fetching Article(s)");
        progress.setMessage("Wasting Time...");
        progress.show();

        /*System.out.println(time);
        if (news) System.out.println("news");
        if (health) System.out.println("health");
        if (finance) System.out.println("finance");
        if (politics) System.out.println("politics");
        if (tech) System.out.println("tech");

        System.out.println("----BEGIN POST-----");*/
        new MyAsyncTask().execute();
//        progress.dismiss();
    }

    public class MyAsyncTask extends AsyncTask<String, Integer, Double>{
        @Override
        protected Double doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                postData();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Double result){
            try {
                parseResponse();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println("PRINTING ARTICLE IN LOADING ");
            for(Article a: articles)
            {
                System.out.print(a.title + " " + a.time);
            }

            Intent i = new Intent(LoadingActivity.this, ArticleActivity.class);

//        Bundle data = new Bundle();
//        data.putParcelableArrayList("articles", articles);
//            i.putExtra("articles", new DataWrapper(articles));
            i.putExtra("articles", articles);
            startActivity(i);

            // transition to article via intent, passing in new article array

        }
    }

    public void postData() throws IOException {


        URL url = new URL("http://timewastr.herokuapp.com/timewastr");
        Map<String,Object> params = new LinkedHashMap();
        params.put("time", Integer.toString(time));
        params.put("news", Boolean.toString(news));
        params.put("health", Boolean.toString(health));
        params.put("finance", Boolean.toString(finance));
        params.put("politics", Boolean.toString(politics));
        params.put("tech", Boolean.toString(tech));

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String,Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"), 8);

        StringBuilder sb = new StringBuilder();

        String line = null;
        while ((line = reader.readLine()) != null)
        {
            sb.append(line + "\n");
        }
        response = sb.toString();
        reader.close();


        /*URL url;
        HttpURLConnection conn;
        try {
            url = new URL("http://timewastr.herokuapp.com/test");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"), 8);

            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            response = sb.toString();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/


        /*HttpClient httpclient = new DefaultHttpClient();
        // specify the URL you want to post to
        HttpPost httppost = new HttpPost("http://timewastr.herukoapp.com/timewastr");
        try {
            // create a list to store HTTP variables and their values
            List nameValuePairs = new ArrayList();
            // add an HTTP variable and value pair
            nameValuePairs.add(new BasicNameValuePair("time", Integer.toString(time)));

            nameValuePairs.add(new BasicNameValuePair("news", Boolean.toString(news)));
            nameValuePairs.add(new BasicNameValuePair("health", Boolean.toString(health)));
            nameValuePairs.add(new BasicNameValuePair("finance", Boolean.toString(finance)));
            nameValuePairs.add(new BasicNameValuePair("politics", Boolean.toString(politics)));
            nameValuePairs.add(new BasicNameValuePair("tech", Boolean.toString(tech)));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));


            // send the variable and value, in other words post, to the URL
            response = httpclient.execute(httppost);
        } catch (ClientProtocolException e) {
            // process execption
        }*/
    }

    public void parseResponse() throws JSONException {
        String tempTitle;
        String tempPublished;
        String tempLink;
        String tempContent;
        String tempPicture;
        String tempAuthor;
        int tempTime;

        JSONObject data = new JSONObject(response);
        int dataLength = data.length();

        Iterator<?> keys = data.keys();

        while (keys.hasNext()) {
            String key = (String)keys.next();
            if (data.get(key) instanceof JSONObject) {
                //Make sure the key is a JSON object, we don't want null values
                JSONObject nestedData = data.getJSONObject(key);
                //Print out each section, you can save them as variables too for later use,
                //Probably push them onto an array for each and then pop them off the stack
                //in a for loop for easier access later


                tempTitle = nestedData.getString("title");
                tempPublished = nestedData.getString("published");
                tempLink = nestedData.getString("link");
                tempContent = nestedData.getString("content");
                tempAuthor = nestedData.getString("author");
                if (nestedData.has("picture")) {
                    tempPicture = nestedData.getString("picture");
                }
                else {
                    tempPicture = null;
                }

                tempTime = new Integer(nestedData.getString("time"));

                Article tempArticle = new Article(tempTitle, tempPublished, tempLink, tempContent, tempPicture, tempAuthor, tempTime);
                articles.add(tempArticle);
            }
        }

    }



}