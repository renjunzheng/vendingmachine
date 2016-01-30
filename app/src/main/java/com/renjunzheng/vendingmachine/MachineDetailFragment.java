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

import java.net.URI;

/**
 * A placeholder fragment containing a simple view.
 */
public class MachineDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    //unique for each loader to use in activity
    private static final int DETAIL_LOADER = 0;

    //private MerchandiseAdapter mMerchandiseAdapter;

    public MachineDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_machine_detail, container, false);
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
        //mMerchandiseAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {//mMerchandiseAdapter.swapCursor(null);
    }
}
