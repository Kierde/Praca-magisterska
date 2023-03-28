package com.example.project1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.GregorianCalendar;

public class WyszukaneBazaPosilkow extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    TabItem tab2, tab3;
    PageAdapterPosilki pagerAdapter;
    String nazwaPosilku;
    GregorianCalendar gregorianCalendar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wyszukane_baza_posilkow);

        nazwaPosilku = getIntent().getStringExtra("nazwaPosilku");
        gregorianCalendar = (GregorianCalendar) getIntent().getSerializableExtra("data");


        tabLayout = (TabLayout) findViewById(R.id.tablayout1);
        tab2 = (TabItem) findViewById(R.id.Tab21);
        tab3 = (TabItem) findViewById(R.id.Tab31);
        viewPager = findViewById(R.id.ViewPager1);


        pagerAdapter = new PageAdapterPosilki(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition()==0){
                    pagerAdapter.notifyDataSetChanged();
                }else if(tab.getPosition()==1) {
                    pagerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }
}