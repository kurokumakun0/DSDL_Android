package tw.edu.ntu.csie.kurokuma.sync;

import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Created by tree on 2016/6/15.
 */
public class CircularArrayAdapter extends ArrayAdapter<String> {

    public static final int HALF_MAX_VALUE = Integer.MAX_VALUE/2;
    public final int MIDDLE;
    private String[] objects;
    public DrawerLayout mDrawer;
    private static final String TAG = CircularArrayAdapter.class.getSimpleName();

    public CircularArrayAdapter(Context context, int textViewResourceId, String[] objects, DrawerLayout drawerLayout)
    {
        super(context, textViewResourceId, objects);
        this.objects = objects;
        MIDDLE = HALF_MAX_VALUE - HALF_MAX_VALUE % objects.length;
        mDrawer = drawerLayout;
    }

    private static class ViewHolder {

        CustomTextView WeaponTextView;

        public ViewHolder(CustomTextView WeaponTextView){
            this.WeaponTextView = WeaponTextView;
        }
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
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
                    (CustomTextView) view
            );
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        String WeaponName = getItem(i % objects.length);
        final int WeaponNo = i%objects.length;

        // Bottom line
        SpannableString content = new SpannableString(WeaponName);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        holder.WeaponTextView.setText(content);
        holder.WeaponTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("Name = ", ((TextView) v).getText().toString());
                Log.d("selected WeaponNo = " + WeaponNo , "");
                MainActivity.Switch_weapon(WeaponNo);
                mDrawer.closeDrawer(GravityCompat.END);
            }
        });

        return view;
    }
}