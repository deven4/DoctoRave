package com.example.doctorave.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.doctorave.R;

import java.util.List;

public class GridViewAdapter extends BaseAdapter {

    Context mContext;
    List<String> images;

    public GridViewAdapter(Context mContext, List<String> images) {
        this.mContext = mContext;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.image_grid_layout, parent, false);

        ImageView img = view.findViewById(R.id.imageView8);
        Glide.with(mContext).load(images.get(position)).into(img);

        return view;
    }

}
