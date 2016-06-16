package tw.edu.ntu.csie.kurokuma.sync;

import android.view.View;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by tree on 2016/6/7.
 */
public class Utils {
    public static void full_screen_mode(View decor_view)   {
        if( android.os.Build.VERSION.SDK_INT >= 19 )  {
            decor_view.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }else   {
            decor_view.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN); // hide status bar
        }
    }

    public static float centerX(View view){
        return ViewHelper.getX(view) + view.getWidth()/2;
    }

    public static float centerY(View view){
        return ViewHelper.getY(view) + view.getHeight()/2;
    }
}
