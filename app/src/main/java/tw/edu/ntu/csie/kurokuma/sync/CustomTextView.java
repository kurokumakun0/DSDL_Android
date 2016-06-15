package tw.edu.ntu.csie.kurokuma.sync;

import android.content.Context;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by tree on 2016/6/15.
 */
public class CustomTextView extends TextView {

    private static final int MAX_INDENT = 100;
    private static final String TAG = CustomTextView.class.getSimpleName();

    public CustomTextView(Context context) {
        super(context);
        super.setTextSize(35f);
        super.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 250);
        super.setLayoutParams(layoutParams);
    }

    public void onDraw(Canvas canvas){
        canvas.save();
        float indent = getIndent(getY());
        //Part of the magic happens here too
        canvas.translate(-indent, 0);
        super.onDraw(canvas);
        canvas.restore();
    }

    public float getIndent(float distance){
        float x_vertex = MAX_INDENT;
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float y_vertex = displayMetrics.heightPixels  / displayMetrics.density;
        double a = ( 0 - x_vertex ) / ( Math.pow(( 0 - y_vertex), 2) ) ;
        float indent = (float) (a * Math.pow((distance - y_vertex), 2) + x_vertex);
        return indent;
    }
}
