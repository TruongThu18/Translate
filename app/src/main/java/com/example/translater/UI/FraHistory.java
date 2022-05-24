package com.example.translater.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.translater.R;
import com.example.translater.repo.TranslateDataBase;

public class FraHistory extends Fragment {
    RecyclerView rv;
    AdapterRv adapterRv;
    SearchView sv;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fra3,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv = view.findViewById(R.id.rv);
        sv = view.findViewById(R.id.sv);
        adapterRv = new AdapterRv(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapterRv);
        adapterRv.addData(TranslateDataBase.getInstance(getContext()).transDao().getAll());
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapterRv.addData(TranslateDataBase.getInstance(getContext()).transDao().getByName(s));
                return false;
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        adapterRv.addData(TranslateDataBase.getInstance(getContext()).transDao().getAll());
    }
}
