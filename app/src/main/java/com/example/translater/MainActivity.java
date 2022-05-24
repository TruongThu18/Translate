package com.example.translater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.translater.UI.AdapterVp;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    ViewPager vp;
    BottomNavigationView nab;
    AdapterVp adapterVp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapterVp = new AdapterVp(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vp = findViewById(R.id.vp);
        vp.setAdapter(adapterVp);
        nab = findViewById(R.id.nab);
        nab.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.mtxt :
                        vp.setCurrentItem(0);
                        break;
                    case R.id.mcap:
                        vp.setCurrentItem(1);
                        break;
                    case R.id.mhis:
                        vp.setCurrentItem(2);
                        break;
                }
                return false;
            }
        });
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        nab.getMenu().findItem(R.id.mtxt).setChecked(true);
                        break;
                    case 1:
                        nab.getMenu().findItem(R.id.mcap).setChecked(true);
                        break;
                    case 2:
                        nab.getMenu().findItem(R.id.mhis).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}