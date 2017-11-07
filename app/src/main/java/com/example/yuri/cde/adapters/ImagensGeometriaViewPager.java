package com.example.yuri.cde.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.yuri.cde.R;

public class ImagensGeometriaViewPager extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private Integer[] geometriaImagens = {R.mipmap.geometria_frente, R.mipmap.geometria_cima};

    public ImagensGeometriaViewPager(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return geometriaImagens.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.images_geometria, null);
        ImageView imageView = view.findViewById(R.id.image_geometria);
        imageView.setImageResource(geometriaImagens[position]);

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}
