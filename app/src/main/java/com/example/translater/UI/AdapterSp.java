package com.example.translater.UI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.translater.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdapterSp extends BaseAdapter {

    List<String> listsp;
    public AdapterSp() {
        listsp = new ArrayList<>();
        listsp.add("English");
        listsp.add("French");
        listsp.add("Germany");
        listsp.add("Korean");
        listsp.add("Urdu");
        listsp.add("Italian");
        listsp.add("Catalan");
        listsp.add("Czech");
        listsp.add("Welsh");
        listsp.add("Hindi");
        listsp.add("Japan");
        listsp.add("Vietnamese");
        listsp.add("Chinese");
    }

    public List<String> getListsp() {
        return listsp;
    }

    @Override
    public int getCount() {
        return listsp.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.item_row_sp, viewGroup,false);
        TextView insp = view.findViewById(R.id.insp);
        insp.setText(listsp.get(i));

        return view;
    }
}
