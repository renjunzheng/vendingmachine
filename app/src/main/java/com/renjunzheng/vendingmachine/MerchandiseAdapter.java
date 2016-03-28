package com.renjunzheng.vendingmachine;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.ImageView;

/**
 * Created by Renjun Zheng on 2016/1/29.
 */
public class MerchandiseAdapter extends CursorAdapter{

    public MerchandiseAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    /**
     * Cache of the children views for a merchandise list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView descriptionView;
        public final TextView numView;
        public final TextView nameView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.merchandise_icon);
            descriptionView = (TextView) view.findViewById(R.id.list_item_merchandise_description_textview);
            numView = (TextView) view.findViewById(R.id.list_item_merchandise_num_textview);
            nameView = (TextView) view.findViewById(R.id.list_item_merchandise_name_textview);
        }
    }

    /*
    Remember that these views are reused as needed.
    */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_merchandise, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.iconView.setImageResource(Utility.getArtResourceForMerchandise(cursor.getString(MachineDetailFragment.COL_ITEM_NAME)));
        viewHolder.descriptionView.setText(cursor.getString(MachineDetailFragment.COL_ITEM_SHORT_DESC));
        viewHolder.numView.setText(cursor.getString(MachineDetailFragment.COL_ITEM_REMAINING_NUM));
        viewHolder.nameView.setText(cursor.getString(MachineDetailFragment.COL_ITEM_NAME));
    }
}
