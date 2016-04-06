package com.renjunzheng.vendingmachine;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import android.annotation.TargetApi;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.renjunzheng.vendingmachine.data.DataContract;

import java.io.IOException;

/**
 * A placeholder fragment containing a simple view.
 */
public class ItemDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "ItemDetailFragment";

    private ListView mListView;
    private ItemAdapter mItemAdapter;
    private View mPurchaseFormView;
    private View mProgressView;
    private UserPurchaseTask mPurchaseTask = null;


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
            DataContract.ItemEntry.COLUMN_SHORT_DESC,
            DataContract.ItemEntry.COLUMN_PRICE
    };

    static final int COL_ITEM_ID = 0;
    static final int COL_ITEM_NAME = 1;
    static final int COL_ITEM_REMAINING_NUM = 2;
    static final int COL_ITEM_SHORT_DESC = 3;
    static final int COL_PRICE = 4;


    public ItemDetailFragment() {
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

        mItemAdapter = new ItemAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.listview_item);
        mListView.setAdapter(mItemAdapter);

        /*
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    Intent intent = new Intent(getActivity(), ItemDetail.class);
                    startActivity(intent);
                }
            }
        });
        */
        Button button;

        button = (Button) rootView.findViewById(R.id.purchase_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                attemptPurchase();
            }
        });

        mProgressView = rootView.findViewById(R.id.purchase_progress);
        mPurchaseFormView = rootView.findViewById(R.id.item_detail_form);

        return rootView;
    }

    //required to implement using loadercallback
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle){
        Log.i(TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if(intent == null){
            return null;
        }

        //return a parameterized cursor
        //call content provider on our behalf
        return new CursorLoader(getActivity(),
                intent.getData(),
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
        mItemAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        //mItemAdapter.swapCursor(null);
    }

    private void attemptPurchase() {
        if (mPurchaseTask != null) {
            return;
        }

        boolean enough = false;

        if (!enough) {
            // There was not enough money to buy, Toast
            Toast.makeText(getActivity(),"You don't have enough money to make purchase!", Toast.LENGTH_SHORT).show();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user purchase.
            showProgress(true);
            int itemid = 0;
            mPurchaseTask = new UserPurchaseTask(itemid, getContext());
            mPurchaseTask.execute((Void) null);
        }
    }

    public class UserPurchaseTask extends AsyncTask<Void, Void, Integer> {
        private int mitemid;
        private Context mContext;

        UserPurchaseTask(int itemid, Context context) {
            mitemid = itemid;
            mContext = context;
        }

        final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(mContext);

        @Override
        protected Integer doInBackground(Void... params) {
            // Not the most elegant way for checking if logged in I think.
            // So basically I have one bool will be true once received server response regarding the validation
            // but I have to use a timer set to 1000 ms so that it didn't check the boolean value too frequently
            // then with the status code server send back, I can determine whether the password is correct.
            // right now no matter what it will tell the password is wrong, instead of more accurate info.

            /*try {

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putBoolean("login_checked", false);
                editor.apply();
                while (!PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("login_checked", false)) {
                    Bundle data = new Bundle();
                    // the action is used to distinguish
                    // different message types on the server
                    data.putString("action", "LOGIN");
                    data.putString("email", mEmail);
                    data.putString("passwd", mPassword);

                    int storedMsgId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("msgId", 0);
                    editor.putInt("msgId", storedMsgId++);
                    editor.apply();

                    String msgId = Integer.toString(storedMsgId);
                    String projectId = getString(R.string.gcm_defaultSenderId);
                    gcm.send(projectId + "@gcm.googleapis.com", msgId, data);
                    SystemClock.sleep(1000);
                }
                editor.putBoolean("login_checked", false);
                editor.apply();
                return PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("login_status_code", 400);
            } catch (IOException e) {
                Log.e(TAG,"IO Exception");
                return 400;
            }*/
            return 0;
        }

        @Override
        protected void onPostExecute(final Integer returnCode) {
            mPurchaseTask = null;
            showProgress(false);

            switch (returnCode) {
                case 400:

                    break;
                case 200:

                    break;
                default:
                    Toast.makeText(getActivity(), "Unable to process your purchase now", Toast.LENGTH_SHORT).show();
                    return;
            }
        }
    }

    /**
     * Shows the progress UI and hides the purchase form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mPurchaseFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mPurchaseFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mPurchaseFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mPurchaseFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
