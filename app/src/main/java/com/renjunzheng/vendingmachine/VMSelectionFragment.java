package com.renjunzheng.vendingmachine;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class VMSelectionFragment extends Fragment {

    public VMSelectionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //building data
        String[] buildings = {
                "ECE Available",
                "MSEE Not Available",
                "WANG HALL Not Available"
        };

        //turn building data into ArrayList
        List<String> machines = new ArrayList<String>(Arrays.asList(buildings));

        //link ArrayList to ArrayAdapter
        final ArrayAdapter<String> machinesAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_machines,
                R.id.list_item_machines_textview,
                machines
        );

        //bind ArrayAdapter with the ListView
        View rootView = inflater.inflate(R.layout.fragment_vmselection, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_machines);
        listView.setAdapter(machinesAdapter);

        //enable ListView click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String machineDetail = machinesAdapter.getItem(position);
                boolean availability = true;
                String site = new String ("");
                for(String parsed: machineDetail.split(" ")){
                    if(parsed.equals("Not")){
                        availability = false;
                        break;
                    }
                    else if(!parsed.equals("Available")){
                        site += " " + parsed;
                    }
                }
                if(availability){
                    Intent intent = new Intent(getActivity(), MachineDetail.class).putExtra(Intent.EXTRA_TEXT, site);
                    startActivity(intent);
                }else{
                    Toast.makeText(getActivity(),"Machine located at " + site + " currently offline.\nTry other machines! ;)", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }
}
