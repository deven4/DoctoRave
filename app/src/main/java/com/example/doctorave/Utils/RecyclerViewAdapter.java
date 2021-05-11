package com.example.doctorave.Utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.doctorave.HomeActivity.PatientDetails;
import com.example.doctorave.ModelClasses.Patient;
import com.example.doctorave.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.mViewHolderClass> {

    Context context;
    ArrayList<Patient> patients;

    public RecyclerViewAdapter(Context context, ArrayList<Patient> patients) {
        this.context = context;
        this.patients = patients;
    }

    @NonNull
    @Override
    public mViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.patient_layout, parent, false);
        return new mViewHolderClass(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolderClass holder, int position) {

        holder.patientName.setText(patients.get(position).getName());
        holder.illness.setText(patients.get(position).getIssueIllness());

//        if (patients.get(position).getImages().size() != 0) {
//            String image = patients.get(position).getImages().get(0).getImage();
//            Glide.with(context).load(image).into(holder.imageView);
//        }

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(Constants.INFLATE_VIEW_PAGER_LAYOUT,
                patients.get(position).getImages());
        holder.viewPager.setAdapter(pagerAdapter);

//         Adding Dot Indicators
        HelperMethods.setUpViewPagerIndicator(context, holder.viewPager, holder.dotLayout,
                patients.get(position).getImages().size());
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }


    public class mViewHolderClass extends RecyclerView.ViewHolder {

        ImageView imageView;
        ViewPager viewPager;
        LinearLayout dotLayout, viewDetails;
        TextView patientName, illness;
        ConstraintLayout constraintLayout;

        public mViewHolderClass(@NonNull View itemView) {
            super(itemView);

//            imageView = itemView.findViewById(R.id.viewPager);
            patientName = itemView.findViewById(R.id.textView8);
            illness = itemView.findViewById(R.id.textView9);
            viewPager = itemView.findViewById(R.id.viewPager);
            viewDetails = itemView.findViewById(R.id.cardView6);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            dotLayout = itemView.findViewById(R.id.linearLayout6);

            viewDetails.setOnClickListener(v -> {
                Intent intent = new Intent(context, PatientDetails.class);
                intent.putExtra(Constants.PATIENT_OBJECT, patients.get(getAdapterPosition()));
                context.startActivity(intent);
            });
        }
    }
}
