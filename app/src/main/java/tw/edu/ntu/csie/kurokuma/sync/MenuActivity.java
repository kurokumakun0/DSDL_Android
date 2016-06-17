package tw.edu.ntu.csie.kurokuma.sync;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Y.C.Lai on 2016/6/17.
 */
public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void goSinglePlayer(View v){
        Intent single = new Intent(MenuActivity.this, MainActivity.class);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            View sharedView = v;
            String transitionName = getString(R.string.blue_transitionName);

            ActivityOptions transitionActivityOptions = null;
            transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(MenuActivity.this, sharedView, transitionName);
            startActivity(single, transitionActivityOptions.toBundle());
        }else {
            startActivity(single);
        }
    }

    public void goMultiplePlayer(View v){
        Intent multiple = new Intent(MenuActivity.this, MultipleActivity.class);
        startActivity(multiple);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Utils.full_screen_mode(getWindow().getDecorView());
    }
}