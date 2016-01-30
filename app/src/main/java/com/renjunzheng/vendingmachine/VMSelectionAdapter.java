package com.renjunzheng.vendingmachine;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Renjun Zheng on 1/29/2016.
 */
public class VMSelectionAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] itemname;
    private final boolean[] availability;
    private final Integer[] imgid = {
        R.drawable.green_pass,
        R.drawable.red_forbidden,
    };

    public VMSelectionAdapter(Activity context, String[] itemname, boolean[] availability) {
        super(context, R.layout.list_item_machines, itemname);
        this.context=context;
        this.itemname=itemname;
        this.availability=availability;
    }

    public View getView(int position, View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_item_machines, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.list_item_machines_textview);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.availability_icon);

        txtTitle.setText(itemname[position]);
        if(availability[position]) imageView.setImageResource(imgid[0]);
        else imageView.setImageResource(imgid[1]);
        return rowView;
    }
}
