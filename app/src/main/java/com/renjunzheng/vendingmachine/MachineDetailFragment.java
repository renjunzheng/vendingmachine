package com.renjunzheng.vendingmachine;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.renjunzheng.vendingmachine.data.DataContract;
import java.net.URI;

/**
 * A placeholder fragment containing a simple view.
 */
public class MachineDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "MachineDetailFragment";
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
            DataContract.ItemEntry.TABLE_NAME + "." + DataContract.ItemEntry._ID,
            DataContract.ItemEntry.COLUMN_ITEM_NAME,
            DataContract.ItemEntry.COLUMN_REMAINING_NUM,
            DataContract.ItemEntry.COLUMN_SHORT_DESC
    };

    static final int COL_ITEM_ID = 0;
    static final int COL_ITEM_NAME = 1;
    static final int COL_ITEM_REMAINING_NUM = 2;
    static final int COL_ITEM_SHORT_DESC = 3;

    private MerchandiseAdapter mMerchandiseAdapter;

    public MachineDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The CursorAdapter will take data from our cursor and populate the ListView

        mMerchandiseAdapter = new MerchandiseAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_machine_detail, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.listview_merchandise);
        mListView.setAdapter(mMerchandiseAdapter);

        // We'll call our MainActivity
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                String temp1 = cursor.getString(COL_ITEM_NAME);
                Log.i(TAG,"t1: "+temp1);
                if (cursor != null) {
                    Intent intent = new Intent(getActivity(), ItemDetail.class)
                            .setData(DataContract.ItemEntry.buildItemWithName(temp1));
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    //required to implement using loadercallback
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle){
        //!!!following is wrong
        Uri detailUri = DataContract.ItemEntry.CONTENT_URI;

        //return a parameterized cursor
        //call content provider on our behalf
        return new CursorLoader(getActivity(),
                detailUri,
                ITEM_COLUMNS,
                null, null, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor){
        mMerchandiseAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMerchandiseAdapter.swapCursor(null);
    }
}
