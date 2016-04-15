package com.renjunzheng.vendingmachine;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.preference.PreferenceManager;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import android.annotation.TargetApi;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.renjunzheng.vendingmachine.data.DataContract;
import com.renjunzheng.vendingmachine.data.DataDbHelper;

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
    private EditText mQuantity;

    private static final int DETAIL_LOADER = 0;

    private static final String[] ITEM_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the user,item,purchased tables in the background
            // (both have an _id column)

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

        mProgressView = rootView.findViewById(R.id.purchase_progress);
        mPurchaseFormView = rootView.findViewById(R.id.item_detail_form);

        mQuantity = (EditText) rootView.findViewById(R.id.purchase_num);

        Button button;

        button = (Button) rootView.findViewById(R.id.buy_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                attemptPurchase();
            }
        });

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

        TextView mTextView = (TextView) mListView.findViewById(R.id.detail_name_textview);

        if(mQuantity.getText().toString() == null || mQuantity.getText().toString().isEmpty()) {
            mQuantity.setError("Invalid quantity to purchase");
            mQuantity.requestFocus();
            return;
        }

        boolean enough = false;



        if (enough) {
            mQuantity.setError("Don't have enough money");
            mQuantity.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user purchase.
            showProgress(true);
            mPurchaseTask = new UserPurchaseTask(mTextView.getText().toString(), getContext(), Integer.parseInt(mQuantity.getText().toString()));
            mPurchaseTask.execute((Void) null);
        }
    }

    public class UserPurchaseTask extends AsyncTask<Void, Void, Integer> {
        private String mName;
        private int mQuant;
        private Context mContext;

        UserPurchaseTask(String name, Context context, int quantity) {
            mName = name;
            mContext = context;
            mQuant = quantity;
        }

        final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(mContext);

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit();
                editor.putBoolean("purchase_verified", false);
                editor.apply();
                Bundle data = new Bundle();
                data.putString("action", "PURCHASE");
                data.putString("item_name", mName);
                data.putString("quantity", Integer.toString(mQuant));
                Log.i(TAG,"quantity:" + Integer.toString(mQuant));
                String email = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString("user_email", "admin@uvm.com");
                if(email.equals("admin@uvm.com"))
                    return -3;
                else {
                    data.putString("email", email);
                    int storedMsgId = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getInt("msgId", 0);
                    editor.putInt("msgId", storedMsgId++);
                    editor.apply();

                    String msgId = Integer.toString(storedMsgId);
                    String projectId = getString(R.string.gcm_defaultSenderId);
                    gcm.send(projectId + "@gcm.googleapis.com", msgId, data);
                    while (!PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getBoolean("purchase_verified", false)) {
                        SystemClock.sleep(1000);
                    }
                    editor.putBoolean("purchase_verified", false);
                    editor.apply();
                    return PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getInt("purchase_status", -4);
                }
            } catch (IOException e) {
                Log.e(TAG,"IO Exception");
                return -5;
            }
        }

        @Override
        protected void onPostExecute(final Integer returnCode) {
            mPurchaseTask = null;
            showProgress(false);

            if(returnCode > 0){
                //Toast.makeText(getActivity(), "Your purchase has been processed! Receipt Code is: " + Integer.toString(returnCode), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), QRCodeDetail.class).putExtra(Intent.EXTRA_TEXT, Integer.toString(returnCode));
                startActivity(intent);
            }else if(returnCode == -1){
                mQuantity.setError("Don't have enough money");
                mQuantity.requestFocus();
            }else{
                Toast.makeText(getActivity(), "Unable to process your purchase now", Toast.LENGTH_SHORT).show();
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
