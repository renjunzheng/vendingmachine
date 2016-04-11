package com.renjunzheng.vendingmachine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.renjunzheng.vendingmachine.data.DataContract;
import com.renjunzheng.vendingmachine.data.DataProvider;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by XPS on 2016/2/25.
 */
public class PurchaseHistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private boolean mVisible;
    private static final int UI_ANIMATION_DELAY = 300;
    private TextView mTextView;

    private static final String TAG = "PurchaseHistoryFragment";
    private ListView mListView;

    //unique for each loader to use in activity
    //this is the loader id
    private static final int DETAIL_LOADER = 0;

    private static final String[] ITEM_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            DataContract.PurchasedEntry.TABLE_NAME + "." + DataContract.PurchasedEntry._ID,
            DataContract.ItemEntry.COLUMN_ITEM_NAME,
            DataContract.PurchasedEntry.COLUMN_RECEIPT_NUM,
            DataContract.PurchasedEntry.COLUMN_ORDER_TIME,
            DataContract.PurchasedEntry.COLUMN_PICK_UP_TIME
    };

    static final int COL_PURCHASED_ID = 0;
    static final int COL_PURCHASED_NAME = 1;
    static final int COL_PURCHASED_RECEIPT = 2;
    static final int COL_PURCHASED_BUY_TIME = 3;
    static final int COL_PURCHASED_PICKUP_TIME = 4;

    private PurchasedAdapter mPurchasedAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*String[] receipt_code = {
                "12345678",
                "23847881",
                "98723507"
        };

        ArrayList<String> codes = new ArrayList<>(Arrays.asList(receipt_code));

        final ArrayAdapter<String> codeAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.list_item_receipt_code,
                R.id.list_item_codes_textview,
                codes
        );*/

        mPurchasedAdapter = new PurchasedAdapter(getActivity(), null, 0);


        View rootView = inflater.inflate(R.layout.fragment_purchase_history, container, false);
        mListView = (ListView) rootView.findViewById(R.id.listview_purchase_history);
        mListView.setAdapter(mPurchasedAdapter);

        //enable ListView click
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                String temp1 = cursor.getString(COL_PURCHASED_RECEIPT);
                Log.i(TAG,"t1: "+temp1);
                if (cursor != null) {
                    Intent intent = new Intent(getActivity(), QRCodeDetail.class).putExtra(Intent.EXTRA_TEXT, temp1);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle){
        return new CursorLoader(getActivity(),
                DataContract.UserEntry.buildUserPurchase(PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString("user_email", "admin@uvm.com")),
                ITEM_COLUMNS,
                null, null, DataContract.PurchasedEntry.COLUMN_ORDER_TIME + " DESC");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor){
        mPurchasedAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mPurchasedAdapter.swapCursor(null);
    }

}
