package timewastr.app;

import android.OnSwipeTouchListener;
import android.app.*;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Zawu on 2/10/14.
 */
public class ArticleActivity extends Activity {

    TextView tv1;
    OnSwipeTouchListener onSwipeTouchListener;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        tv1 = (TextView)findViewById(R.id.tv1);

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

        String s = "";

        for(int x=0;x<=100;x++){
            s += "Line: "+String.valueOf(x)+"\n";
        }

        String x = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus tristique congue neque, nec dignissim mauris sollicitudin vel. Integer a turpis purus. In faucibus est mi, ac congue quam congue vitae. Vestibulum pretium quis mauris laoreet suscipit. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Etiam arcu metus, varius id malesuada eget, tincidunt non eros. Vivamus nibh erat, scelerisque vulputate accumsan vitae, dignissim nec leo. Mauris rhoncus ut felis ultricies ultricies.\n" +
                "\n" +
                "Nunc et leo enim. Nullam eget tempus turpis.\\ Proin lorem dolor, lacinia et placerat a, eleifend et ante. Sed non varius magna. Sed convallis leo a ultrices suscipit. Mauris a sapien quis enim molestie accumsan. Suspendisse est lacus, bibendum sed metus id, venenatis semper sapien. Vivamus faucibus quam lacinia, fermentum elit vel, tristique tortor. Maecenas fringilla eros ut ligula egestas aliquam.\n" +
                "\n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam vitae ante urna. Suspendisse potenti. Nam pretium tortor euismod sapien consectetur egestas. Pellentesque iaculis enim sit amet quam accumsan, sit amet facilisis libero malesuada. Proin elementum laoreet diam. Etiam suscipit tincidunt tellus, a aliquam augue ornare vel. Ut accumsan molestie cursus. Vivamus auctor felis in iaculis vulputate. Pellentesque odio tellus, ullamcorper vitae tortor vel, condimentum sollicitudin felis. In non lacinia enim.\n" +
                "\n" +
                "Nunc tincidunt elit tortor, vel egestas nunc vestibulum a. Curabitur in ante massa. Donec ut dapibus diam, vel accumsan sapien. Sed ullamcorper dolor dui. Nunc in orci eget ante semper semper sed id magna. Phasellus suscipit faucibus purus id laoreet. Proin eget est a enim vehicula vehicula et ac dui.\n" +
                "\n" +
                "Quisque in felis luctus, dignissim urna nec, condimentum augue. Curabitur gravida vitae massa ut condimentum. Sed fringilla augue viverra erat suscipit posuere. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Aenean bibendum laoreet facilisis. Integer vel sem convallis, mattis augue vitae, egestas est. Duis molestie arcu ut orci elementum bibendum. Phasellus vulputate vel quam at mollis. Vestibulum luctus vestibulum mollis. Quisque pulvinar tortor eget leo pulvinar mollis. Nam non risus risus. Quisque posuere eros leo, sed bibendum risus egestas in. Mauris sit amet diam et purus pretium faucibus.\"\n";

        tv1.setMovementMethod(new ScrollingMovementMethod());

        tv1.setText(s);

    }

}
