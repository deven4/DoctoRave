package com.example.doctorave.HomeActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.doctorave.ModelClasses.Patient;
import com.example.doctorave.R;
import com.example.doctorave.Utils.Constants;
import com.example.doctorave.Utils.HelperMethods;

public class ImageViewer extends AppCompatActivity {

    Patient patient;
    Toolbar toolbar;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        toolbar = findViewById(R.id.toolbar5);
        imageView = findViewById(R.id.imageView9);

        getWindow().setStatusBarColor(getResources().getColor(R.color.black, null));

        Intent intent = getIntent();
        String uri = intent.getStringExtra(Constants.IMAGE_URI);
        patient = intent.getParcelableExtra(Constants.PATIENT_OBJECT);
        if (uri != null && patient != null) {
            Glide.with(this).load(uri).into(imageView);
            toolbar.setTitle(patient.getName());
        } else
            HelperMethods.showToastInCenter(this);


        toolbar.setNavigationOnClickListener(v -> finish());
    }
}