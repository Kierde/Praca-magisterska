package com.example.project1;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.View;

import java.util.GregorianCalendar;

public class Komunikator extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    TabItem tab2, tab3;
    PageAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komunikator);

        tabLayout =(TabLayout) findViewById(R.id.tablayout2);
        tab2 =(TabItem) findViewById(R.id.Tab22);
        tab3 =(TabItem) findViewById(R.id.Tab31);
        viewPager =(ViewPager) findViewById(R.id.ViewPager2);

        pagerAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
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