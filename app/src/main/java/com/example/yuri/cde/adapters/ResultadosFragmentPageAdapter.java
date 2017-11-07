package com.example.yuri.cde.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.yuri.cde.fragments.saida.DeslocamentosFragment;
import com.example.yuri.cde.fragments.saida.ReacoesFragment;
import com.example.yuri.cde.fragments.saida.RelatorioFragment;

public class ResultadosFragmentPageAdapter extends FragmentStatePagerAdapter {

    private String[] mTabTitles;

    public ResultadosFragmentPageAdapter (FragmentManager fm, String[] mTabTitles) {
        super(fm);
        this.mTabTitles = mTabTitles;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){

            case 0:

                return new RelatorioFragment();

            case 1:

                return new ReacoesFragment();

            case 2:

                return new DeslocamentosFragment();

            default:

                return new RelatorioFragment();
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