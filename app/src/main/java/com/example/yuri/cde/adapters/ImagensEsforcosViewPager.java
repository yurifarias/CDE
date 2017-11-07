package com.example.yuri.cde.adapters;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.yuri.cde.R;

public class ImagensEsforcosViewPager extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private Integer[] esforcosImagens = {R.mipmap.componentes_de_forca, R.mipmap.componentes_de_momento};

    public ImagensEsforcosViewPager(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return esforcosImagens.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.images_esforcos, null);
        ImageView imageView = view.findViewById(R.id.image_esforcos);
        imageView.setImageResource(esforcosImagens[position]);

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        FragmentManager manager = ((android.app.Fragment) object).getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove((android.app.Fragment) object);
        transaction.commit();

        super.destroyItem(container, position, object);
    }
}
