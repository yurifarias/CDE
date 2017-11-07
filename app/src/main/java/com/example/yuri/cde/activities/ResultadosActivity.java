package com.example.yuri.cde.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.yuri.cde.R;
import com.example.yuri.cde.adapters.ResultadosFragmentPageAdapter;

public class ResultadosActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTabLayout = findViewById(R.id.tab_resultados);
        mViewPager = findViewById(R.id.resultados_view_pager);

        mViewPager.setAdapter(new ResultadosFragmentPageAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.resultados_tab)));
        mViewPager.setOffscreenPageLimit(2);

        mTabLayout.setupWithViewPager(mViewPager);
    }
}