package android;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Zawu on 3/13/14.
 */
public class DataWrapper implements Serializable {

    private ArrayList<Article> articles;

    public DataWrapper(ArrayList<Article> data) {
        this.articles = data;
    }

    public ArrayList<Article> getArticles() {
        return this.articles;
    }

}