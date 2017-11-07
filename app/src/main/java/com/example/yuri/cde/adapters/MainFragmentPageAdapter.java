package com.example.yuri.cde.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.yuri.cde.fragments.entrada.CaracteristicasFragment;
import com.example.yuri.cde.fragments.entrada.EsforcosFragment;
import com.example.yuri.cde.fragments.entrada.GeometriaFragment;
import com.example.yuri.cde.fragments.entrada.InicioFragment;

public class MainFragmentPageAdapter extends FragmentPagerAdapter {

    private String[] mTabTitles;

    public MainFragmentPageAdapter (FragmentManager fm, String[] mTabTitles) {
        super(fm);
        this.mTabTitles = mTabTitles;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){

            case 0:

                return new InicioFragment();

            case 1:

                return new CaracteristicasFragment();

            case 2:

                return new EsforcosFragment();

            case 3:

                return new GeometriaFragment();

            default:

                return new InicioFragment();
        }
    }

    @Override
    public int getCount() {
        return this.mTabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return this.mTabTitles[position];
    }
}