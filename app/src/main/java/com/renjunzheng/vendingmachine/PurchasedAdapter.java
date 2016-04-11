package com.renjunzheng.vendingmachine;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Renjun Zheng on 2016/1/29.
 */
public class PurchasedAdapter extends CursorAdapter{

    public PurchasedAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    /**
     * Cache of the children views for a merchandise list item.
     */
    public static class ViewHolder {
        public final TextView nameView;
        public final TextView receiptView;
        public final TextView buyTimeView;
        public final TextView pickupTimeView;

        public ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.list_item_purchases_name);
            receiptView = (TextView) view.findViewById(R.id.list_item_purchases_receipt);
            buyTimeView = (TextView) view.findViewById(R.id.list_item_purchases_buy_time);
            pickupTimeView = (TextView) view.findViewById(R.id.list_item_purchases_pickup_time);
        }
    }

    /*
    Remember that these views are reused as needed.
    */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_purchases, parent, false);
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

        viewHolder.nameView.setText(cursor.getString(PurchaseHistoryFragment.COL_PURCHASED_NAME));
        viewHolder.receiptView.setText(cursor.getString(PurchaseHistoryFragment.COL_PURCHASED_RECEIPT));
        viewHolder.buyTimeView.setText(cursor.getString(PurchaseHistoryFragment.COL_PURCHASED_BUY_TIME));
        viewHolder.pickupTimeView.setText(cursor.getString(PurchaseHistoryFragment.COL_PURCHASED_PICKUP_TIME));
    }
}
