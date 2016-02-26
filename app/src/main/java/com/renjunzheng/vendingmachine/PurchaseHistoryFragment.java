package com.renjunzheng.vendingmachine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by XPS on 2016/2/25.
 */
public class PurchaseHistoryFragment extends Fragment {

    private boolean mVisible;
    private static final int UI_ANIMATION_DELAY = 300;
    private TextView mTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String[] receipt_code = {
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
        );

        //bind ArrayAdapter with the ListView
        View rootView = inflater.inflate(R.layout.fragment_purchase_history, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_purchase_history);
        listView.setAdapter(codeAdapter);

        //enable ListView click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String code = codeAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), QRCodeDetail.class).putExtra(Intent.EXTRA_TEXT, code);
                startActivity(intent);
            }
        });

        return rootView;
    }


}
