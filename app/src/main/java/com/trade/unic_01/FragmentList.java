package com.trade.unic_01;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentList extends ListFragment {







    public FragmentList() {
        // Required empty public constructor

    }

    @Override
    public void onAttach(Context context) {


        super.onAttach(context);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {


        ArrayList<String> dataToDisplay=new ArrayList<String>();
        //add categories to the arraylist


        setListAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, dataToDisplay));

        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }
}