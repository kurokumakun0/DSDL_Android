package tw.edu.ntu.csie.kurokuma.sync;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by tree on 2016/6/15.
 */
public class HalfCircleListView extends ListView implements AbsListView.OnScrollListener {

    boolean refresh = false;

    public HalfCircleListView(Context context) {
        super(context);
        super.setOnScrollListener(this);
    }

    public HalfCircleListView(Context context, AttributeSet set) {
        super(context, set);
        super.setOnScrollListener(this);
        //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //super.setLayoutParams(layoutParams);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        //Ignored
        Log.d("ss = " + scrollState, "");
        if( scrollState == MotionEvent.ACTION_SCROLL)   {
            refresh = true;
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (visibleItemCount > 0 ){

            final int midPosition = visibleItemCount/2;

            CustomLinearLayout listItem = (CustomLinearLayout) getChildAt(midPosition);
            //CustomTextView listItemText = (CustomTextView) listItem.findViewById(R.id.cTextview);
            // TODO: this line cause cannot click item inside linearlayout
            // QAQQ
            //ViewGroup.LayoutParams p = listItem.getLayoutParams();
            //p.height = 250;
            //listItem.setLayoutParams(p);
            //listItem.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 250));
            //           listItemText.setTextSize(35f);
            //listItemText.setPadding(0, 25, 0, 25);
            //listItemText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 250));
            //listItemText.requestLayout();
            //listItemText.setMinHeight(250);
            //listItem.requestLayout();

            int count = visibleItemCount;
            while (count >= 0){ // Here we need to loop through and make sure
                // all recycled items are returned to their
                // original height.
                CustomLinearLayout item = (CustomLinearLayout) getChildAt(count);
                CustomTextView itemText = null;
                if( item != null )
                    itemText = (CustomTextView) item.findViewById(R.id.fab);
                if( itemText != null )  {
                    if( count != midPosition )  {
                        //ViewGroup.LayoutParams params = item.getLayoutParams();
                        //params.height = 150;
                        //item.setLayoutParams(params);
                        //item.setLayoutParams(new AbsListView.LayoutParams(params.width, 150));
                        //           itemText.setTextSize(25f);
                        //itemText.setPadding(0, 15, 0, 15);
                        //itemText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 150));
                        //itemText.requestLayout();
                        //item.requestLayout();
                        //Log.d("side has listener " + itemText.hasOnClickListeners() ,"");
                    }
                }
                count--;
            }
            refresh = false;
            //setLayoutParams();
        }
        //Part of the magic happens here
        absListView.invalidateViews();
    }
}
