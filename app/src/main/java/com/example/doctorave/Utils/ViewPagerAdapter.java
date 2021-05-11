package com.example.doctorave.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.doctorave.ModelClasses.PatientImages;
import com.example.doctorave.R;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    String Type;
    List<PatientImages> images;

    public ViewPagerAdapter(String Type, List<PatientImages> images) {
        this.images = images;
        this.Type = Type;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return object == view;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View view;
        LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (Type.equals(Constants.INFLATE_VIEW_PAGER_2_LAYOUT))
            view = inflater.inflate(R.layout.view_pager_layout_2, container, false);
        else
            view = inflater.inflate(R.layout.view_pager_layout, container, false);

        ImageView imgRes = view.findViewById(R.id.imageView6);
        Glide.with(container.getContext()).load(images.get(position).getImage()).into(imgRes);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
