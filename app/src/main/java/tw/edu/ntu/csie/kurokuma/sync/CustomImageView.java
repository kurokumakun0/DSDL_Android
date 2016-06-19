package tw.edu.ntu.csie.kurokuma.sync;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by tree on 2016/6/19.
 */
public class CustomImageView extends ImageView {

    private static final int MAX_INDENT = 125;

    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomLinearLayout.LayoutParams layoutParams = new CustomLinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 80);
        layoutParams.gravity = Gravity.END;
        super.setLayoutParams(layoutParams);
        //super.setBackgroundColor(Color.CYAN);
    }

    @Override
    public void onDraw(Canvas canvas){
        canvas.save();

        int[] position = new int[2];
        getLocationInWindow(position);
        //Rect rect = new Rect();
        //getGlobalVisibleRect(rect);
        //getY() + getHeight()/2
        float indent = getIndent(position[1] + getHeight()/2);
        //Log.d("indent = " + indent, "Y = " + getY() + ", height = " + getHeight());
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
        //Log.d("a = " + a, "");
        float indent = (float) (a * Math.pow((distance - y_vertex), 2) + x_vertex);
        //return (indent >= MIN_INDENT) ? indent : MIN_INDENT;
        return indent;
    }
}
