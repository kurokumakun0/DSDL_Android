package tw.edu.ntu.csie.kurokuma.sync;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.roger.match.library.MatchTextView;

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
        startActivity(single);
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