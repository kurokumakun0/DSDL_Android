package tw.edu.ntu.csie.kurokuma.sync;

import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
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
    private static final int mResourceId = R.layout.weapon_list_item;  //自定的layout
    private LayoutInflater mInflater;
    private int[] icon_resourceID = new int[] {
            R.drawable.cus_bullet,
            R.drawable.ray,
            R.drawable.lightning,
            R.drawable.ultimate
    };

    public CircularArrayAdapter(Context context, String[] objects, DrawerLayout drawerLayout)
    {
        super(context, mResourceId, objects);
        this.objects = objects;
        MIDDLE = HALF_MAX_VALUE - HALF_MAX_VALUE % objects.length;
        mDrawer = drawerLayout;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private static class ViewHolder {

        CustomTextView WeaponTextView;
        CustomImageView IconImageView;

        public ViewHolder(CustomTextView WeaponTextView, CustomImageView ICON){
            this.WeaponTextView = WeaponTextView;
            this.IconImageView = ICON;
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
            view = mInflater.inflate(mResourceId, viewGroup, false);
            holder = new ViewHolder(
                    (CustomTextView) view.findViewById(R.id.fab),
                    (CustomImageView) view.findViewById(R.id.icon)
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
//        holder.WeaponTextView.setText(content);
//        holder.WeaponTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Log.d("Name = ", ((TextView) v).getText().toString());
//                Log.d("selected WeaponNo = " + WeaponNo , "");
//                MainActivity.Switch_weapon(WeaponNo);
//                mDrawer.closeDrawer(GravityCompat.END);
//            }
//        });
        holder.IconImageView.setImageResource(icon_resourceID[WeaponNo]);
        holder.IconImageView.setOnClickListener(new View.OnClickListener() {
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