package com.renjunzheng.vendingmachine;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by XPS on 2016/4/3.
 */
public class ItemAdapter extends CursorAdapter {

    private static final String TAG = "ItemAdapter";
    public ItemAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    /**
     * Cache of the children views for a merchandise list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView descriptionView;
        public final TextView quantityView;
        public final TextView nameView;
        public final TextView priceView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.detail_icon);
            descriptionView = (TextView) view.findViewById(R.id.detail_short_desc_textview);
            quantityView = (TextView) view.findViewById(R.id.detail_quantity_textview);
            nameView = (TextView) view.findViewById(R.id.detail_name_textview);
            priceView = (TextView) view.findViewById(R.id.detail_price_textview);
        }
    }

    /*
    Remember that these views are reused as needed.
    */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_item, parent, false);
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
        String name = cursor.getString(ItemDetailFragment.COL_ITEM_NAME);
        Log.i(TAG,name);
        int drawableid= Utility.getArtResourceForMerchandise(name);
        Log.i(TAG,Integer.toString(drawableid));
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.iconView.setImageResource(drawableid);
        viewHolder.descriptionView.setText(cursor.getString(ItemDetailFragment.COL_ITEM_SHORT_DESC));
        viewHolder.quantityView.setText(cursor.getString(ItemDetailFragment.COL_ITEM_REMAINING_NUM));
        viewHolder.nameView.setText(cursor.getString(ItemDetailFragment.COL_ITEM_NAME));
        viewHolder.priceView.setText(cursor.getString(ItemDetailFragment.COL_PRICE));
    }
}
