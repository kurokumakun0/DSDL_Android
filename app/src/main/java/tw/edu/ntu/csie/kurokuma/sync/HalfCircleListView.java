package tw.edu.ntu.csie.kurokuma.sync;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by tree on 2016/6/15.
 */
public class HalfCircleListView extends ListView implements AbsListView.OnScrollListener {

    public HalfCircleListView(Context context) {
        super(context);
        setOnScrollListener(this);
    }

    public HalfCircleListView(Context context, AttributeSet set) {
        super(context, set);
        setOnScrollListener(this);
        //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //super.setLayoutParams(layoutParams);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        //Ignored
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        //Part of the magic happens here
        absListView.invalidateViews();
    }
}
