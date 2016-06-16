package tw.edu.ntu.csie.kurokuma.sync;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.roger.match.library.MatchTextView;

public class Opening extends AppCompatActivity {

    MatchTextView matchTextView;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        matchTextView = (MatchTextView) findViewById(R.id.mTextView);

        Utils.full_screen_mode(getWindow().getDecorView());

        if( matchTextView != null ) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent main = new Intent(Opening.this, MainActivity.class);
                    main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(main);
                }
            }, 5000);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Utils.full_screen_mode(getWindow().getDecorView());
    }
}
