package tw.edu.ntu.csie.kurokuma.sync;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by tree on 2016/6/15.
 */
public class CircularArrayAdapter extends ArrayAdapter {

    public static final int HALF_MAX_VALUE = Integer.MAX_VALUE/2;
    public final int MIDDLE;
    private String[] objects;
    private static final String TAG = CircularArrayAdapter.class.getSimpleName();

    public CircularArrayAdapter(Context context, int textViewResourceId, String[] objects)
    {
        super(context, textViewResourceId, objects);
        this.objects = objects;
        MIDDLE = HALF_MAX_VALUE - HALF_MAX_VALUE % objects.length;
    }

    private static class ViewHolder {

        TextView WeaponName;

        public ViewHolder(TextView txtDate){
            this.WeaponName = txtDate;
        }
    }

    @Override
    public int getCount()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public String getItem(int position)
    {
        return objects[position % objects.length];
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

        String itemViewType = getItem(i % objects.length);
        //Log.d(TAG, itemViewType + " " + i);
        holder.WeaponName.setText(itemViewType);
        holder.WeaponName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Name = ", ((TextView) v).getText().toString());
            }
        });

        return view;
    }
}