package com.example.translater.UI;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class AdapterVp  extends FragmentStatePagerAdapter {
    public AdapterVp(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new FraText();
            case 1: return new FraCapture();
            case 2: return new FraHistory();
        }
        return new FraText();
    }

    @Override
    public int getCount() {
        return 3;
    }
}
