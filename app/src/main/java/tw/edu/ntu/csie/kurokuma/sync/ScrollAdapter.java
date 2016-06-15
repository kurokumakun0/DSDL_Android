package tw.edu.ntu.csie.kurokuma.sync;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by tree on 2016/6/15.
 */
public class ScrollAdapter extends BaseAdapter {

    private List<String> scrollViews;
    private static final String TAG = ScrollAdapter.class.getSimpleName();

    public ScrollAdapter(List<String> scrollViews){

        this.scrollViews = scrollViews;
    }

    /*private view holder class*/
    private class ViewHolder {

        TextView WeaponName;

        public ViewHolder(TextView txtDate){
            this.WeaponName = txtDate;
        }
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String getItem(int i) {
        return scrollViews.get(i % scrollViews.size());
    }

    @Override
    public long getItemId(int i) {
        return i % scrollViews.size();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder = null;

        if(view == null){
            view = new CustomTextView(viewGroup.getContext());
            holder = new ViewHolder(
                    (TextView) view
            );
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        String itemViewType = getItem(i % scrollViews.size());
        Log.d(TAG, itemViewType + " " + i);
        holder.WeaponName.setText(itemViewType);

        return view;
    }
}
