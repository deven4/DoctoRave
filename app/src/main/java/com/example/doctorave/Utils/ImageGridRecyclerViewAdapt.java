package com.example.doctorave.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doctorave.HomeActivity.ImageViewer;
import com.example.doctorave.ModelClasses.Patient;
import com.example.doctorave.R;

import java.util.List;

public class ImageGridRecyclerViewAdapt extends RecyclerView.Adapter<ImageGridRecyclerViewAdapt.mViewHolderClass> {

    private static final String TAG = "RECYCLER_ADAPT";
    Context context;
    Patient patient;
    List<ImageGridModel> imageList;
    public boolean isSelected = false;

    ImageGridInterface mListener;

    public ImageGridRecyclerViewAdapt(Context context, List<ImageGridModel> imageList, Patient patient) {
        this.context = context;
        this.patient = patient;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public mViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.image_grid_recycler_view_layout, parent, false);
        return new mViewHolderClass(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolderClass holder, int position) {

        holder.textView.setText(imageList.get(position).getDate());
        GridViewAdapter adapter = new GridViewAdapter(context, imageList.get(position).getImages());
        holder.gridView.setAdapter(adapter);
        holder.gridView.setExpanded(true);
        holder.gridView.setNumColumns(getNoOfCols(120));

    }


    public int getNoOfCols(float colWidth) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (screenWidth / colWidth + 0.5);
    }


    public void setLongClickListener(ImageGridInterface mListener) {
        this.mListener = mListener;
    }


    @Override
    public int getItemCount() {
        return imageList.size();
    }


    public class mViewHolderClass extends RecyclerView.ViewHolder {

        TextView textView;
        public ExpandableGridView gridView;

        public mViewHolderClass(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView25);
            gridView = itemView.findViewById(R.id.gridView);

            gridView.setOnItemClickListener((parent, view, position1, id) -> {

                if (!isSelected)
                    openImageViewActivity(view, position1);
                else
                    mListener.onItemLongClick(view, position1, getAdapterPosition());

            });

            gridView.setOnItemLongClickListener((parent, view, position, id) -> {
                isSelected = true;
                mListener.onItemLongClick(view, position, getAdapterPosition());
                return false;
            });
        }

        private void openImageViewActivity(View view, int position1) {

            String imageUri = imageList.get(getAdapterPosition()).getImages().get(position1);
            if (imageUri != null) {
                Intent intent = new Intent(context, ImageViewer.class);
                intent.putExtra(Constants.IMAGE_URI, imageUri);
                intent.putExtra(Constants.PATIENT_OBJECT, patient);
                ImageView imageView = view.findViewById(R.id.imageView8);
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                        .makeSceneTransitionAnimation((Activity) context, imageView, "Image");
                context.startActivity(intent, optionsCompat.toBundle());
            } else
                HelperMethods.showToastInCenter(context, "Something went wrong. Please try again later.");
        }
    }


    public interface ImageGridInterface {
        void onItemLongClick(View view, int position, int adapterPosition);
    }
}
