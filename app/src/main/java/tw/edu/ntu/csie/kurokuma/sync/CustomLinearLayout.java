package tw.edu.ntu.csie.kurokuma.sync;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.LinearLayout;

/**
 * Created by tree on 2016/6/19.
 */
public class CustomLinearLayout extends LinearLayout{

    private static final int MAX_INDENT = 0;

    public CustomLinearLayout(Context context) {
        super(context);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150);
//        super.setLayoutParams(layoutParams);
//        super.setGravity(Gravity.END);
//        super.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
//        super.setBackground(ContextCompat.getDrawable(context, R.drawable.weapon_background));
    }

    public CustomLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150);
//        super.setLayoutParams(layoutParams);
        //super.setGravity(Gravity.CENTER|Gravity.END);
//        super.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
//        super.setBackground(ContextCompat.getDrawable(context, R.drawable.weapon_background));
    }

    @Override
    public void onDraw(Canvas canvas){
        canvas.save();

        float indent = getIndent(getY() + getHeight()/2);
        Log.d("indent = " + indent, "");

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
