package com.renjunzheng.vendingmachine;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.net.URI;

/**
 * A placeholder fragment containing a simple view.
 */
public class MachineDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private GridView mGridView;

    //unique for each loader to use in activity
    private static final int DETAIL_LOADER = 0;

    static final int COL_MERCHANDISE_ID = 0;
    static final int COL_MERCHANDISE_DESC = 1;

    private MerchandiseAdapter mMerchandiseAdapter;

    public MachineDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // The ForecastAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mMerchandiseAdapter = new MerchandiseAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_machine_detail, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        mGridView = (GridView) rootView.findViewById(R.id.gridview_merchandise);
        mGridView.setAdapter(mMerchandiseAdapter);
        // We'll call our MainActivity
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    //TODO: click listener
                }
            }
        });

        return rootView;
    }

    //required to implement using loadercallback
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle){
        //TODO: get the URI for the detail using the contract, get the sortOrder from the contract as well
        Uri tempUri = Uri.EMPTY;
        String sortOrder = new String(" ");
        return new CursorLoader(getActivity(),
                tempUri,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor){
        mMerchandiseAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {mMerchandiseAdapter.swapCursor(null);
    }
}
