package tw.edu.ntu.csie.kurokuma.sync;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by tree on 2016/6/15.
 */
public class CustomTextView extends TextView {

    private static final int MAX_INDENT = 175;
    private static final int MIN_INDENT = 50;
    private static final String TAG = CustomTextView.class.getSimpleName();

    public CustomTextView(Context context) {
        super(context);
        super.setTextSize(35f);
        super.setGravity(Gravity.CENTER|Gravity.END);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
        super.setLayoutParams(layoutParams);
        super.setTextColor(Color.WHITE);
        super.setSingleLine();
        super.setFocusable(false);
        super.setClickable(false);
        super.setFocusableInTouchMode(false);
        //super.setBackground(ContextCompat.getDrawable(context, R.drawable.weapon_background));
    }

    public CustomTextView(Context context, AttributeSet set) {
        super(context, set);
    }

    @Override
    public void onDraw(Canvas canvas){
        canvas.save();

        //int[] position = new int[2];
        //getLocationOnScreen(position);
        //Rect rect = new Rect();
        //getGlobalVisibleRect(rect);

        float indent = getIndent(getY() + getHeight()/2);
        //Log.d("indent = " + indent, "Y = " + getY() + ", height = " + getHeight());
        //Part of the magic happens here too
        canvas.translate(-indent, 0);
        super.onDraw(canvas);
        canvas.restore();

        //Log.d("", "mText onDraw");
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
