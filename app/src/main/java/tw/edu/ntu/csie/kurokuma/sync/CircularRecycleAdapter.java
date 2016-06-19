package tw.edu.ntu.csie.kurokuma.sync;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by tree on 2016/6/15.
 */
public class CircularRecycleAdapter extends RecyclerView.Adapter<CircularRecycleAdapter.ViewHolder>{

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public CustomTextView WeaponName;

        public ViewHolder(View itemView){
            super(itemView);
            WeaponName = (CustomTextView) itemView.findViewById(R.id.fab);
        }
    }

    public class listener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newstate) {
            //Ignored
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            //Part of the magic happens here
            recyclerView.invalidateItemDecorations();
        }
    }

    List<String > mWeaponList;
    RecyclerView mRecyclerview;

    public CircularRecycleAdapter(List<String> weaponList, RecyclerView recyclerView)   {
        mWeaponList = weaponList;
        mRecyclerview = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        View contactView = LayoutInflater.from(context).inflate(R.layout.weapon_list_item, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //mRecyclerview.invalidate();
        //holder.WeaponTextView.getY();


        LinearLayoutManager layoutManager = ((LinearLayoutManager)mRecyclerview.getLayoutManager());
        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        int last = layoutManager.findLastVisibleItemPosition();
        Log.d("first = " + firstVisiblePosition, "last = " + last);

        holder.WeaponName.setText(mWeaponList.get(position % mWeaponList.size()));
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }
}
