package tw.edu.ntu.csie.kurokuma.sync;

import android.support.v7.widget.RecyclerView;

/**
 * Created by tree on 2016/6/16.
 */
public class CustomLayoutManager extends RecyclerView.LayoutManager {
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }
}
