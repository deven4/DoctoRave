package com.example.doctorave.HomeActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorave.ModelClasses.Patient;
import com.example.doctorave.ModelClasses.PatientImages;
import com.example.doctorave.R;
import com.example.doctorave.Utils.Constants;
import com.example.doctorave.Utils.HelperMethods;
import com.example.doctorave.Utils.LoadingDialog;
import com.example.doctorave.Utils.ViewPagerAdapter;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;

import static com.example.doctorave.Utils.Constants.PATIENT_DATA_CHANGED;

public class PatientDetails extends AppCompatActivity {

    private static final String TAG = "PATIENT_DETAIL";

    Patient patient;
    Toolbar toolbar;
    ViewPager viewPager;
    CardView viewPhotos;
    ImageView genderIcon;
    CoordinatorLayout coordinatorLayout;
    LinearLayout viewPagerIndicatorLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView illness, appointmentDate, age, email,
            phoneNo, comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);

        age = findViewById(R.id.textView16);
        toolbar = findViewById(R.id.toolbar3);
        email = findViewById(R.id.textView18);
        illness = findViewById(R.id.textView12);
        phoneNo = findViewById(R.id.textView20);
        comments = findViewById(R.id.textView23);
        viewPhotos = findViewById(R.id.cardView5);
        viewPager = findViewById(R.id.viewPager2);
        genderIcon = findViewById(R.id.textView15);
        appointmentDate = findViewById(R.id.textView14);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        viewPagerIndicatorLayout = findViewById(R.id.linearLayout7);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);


        toolbar.setNavigationOnClickListener(v -> finish());
        Menu menu = toolbar.getMenu();
        menu.findItem(R.id.edit_icon).setOnMenuItemClickListener(item -> {
            Intent intent = new Intent(this, AddNewPatient.class);
            intent.putExtra(Constants.PATIENT_OBJECT, patient);
            startActivityForResult(intent, PATIENT_DATA_CHANGED);
            return true;
        });
        menu.findItem(R.id.delete).setOnMenuItemClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm Delete");
            builder.setMessage("Are you sure you want to delete patient?");
            builder.setPositiveButton("Yes", (dialog, which) -> {

                deletePatient();
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                for (PatientImages patientImages : patient.getImages()) {
                    firebaseStorage.getReferenceFromUrl(patientImages.getImage())
                            .delete().addOnCompleteListener(task -> {
                        if (!task.isSuccessful())
                            HelperMethods.showToastInCenter(this,
                                    Objects.requireNonNull(task.getException()).getMessage());
                    });
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.create().show();
            return true;
        });


        /** Get Patient's Data**/
        if (getIntentData())
            getDataFromFireStore();
        else {
            HelperMethods.showToastInCenter(this, "Something went wrong. please try again later.");
            finish();
        }


        viewPhotos.setOnClickListener(v -> {
            Intent intent = new Intent(PatientDetails.this, ImageGrid.class);
            intent.putExtra(Constants.PATIENT_OBJECT, patient);
            startActivityForResult(intent, PATIENT_DATA_CHANGED);
        });
    }


    private void deletePatient() {

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null)
            FirebaseFirestore.getInstance()
                    .collection(getString(R.string.DB_USERS))
                    .document(uid)
                    .collection(getString(R.string.DB_USERS_PATIENTS))
                    .document(patient.getPatientId())
                    .delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    HelperMethods.showToastInCenter(this, "Patient Deleted Successfully.");
                    finish();
                } else
                    HelperMethods.showToastInCenter(this, Objects.requireNonNull(task.getException()).getMessage());
            });
    }


    private boolean getIntentData() {

        Intent intent = getIntent();
        patient = intent.getParcelableExtra(Constants.PATIENT_OBJECT);
        return patient != null;
    }


    private void setWidgets() {

        appointmentDate.setText(getString(R.string.AppointTxt, patient.getAppointmentDate()));
        illness.setText(getString(R.string.IllnessTxt, patient.getIssueIllness()));
        age.setText(getString(R.string.AgeTxt, patient.getAge(), patient.getGender()));
        collapsingToolbarLayout.setTitle(patient.getName());
        phoneNo.setText(patient.getMobileNumber());
        email.setText(patient.getEmailId());
        comments.setText(patient.getComments());

        collapsingToolbarLayout.setExpandedTitleTypeface(Typeface.DEFAULT_BOLD);

        if (patient.getGender().equals("Female"))
            genderIcon.setImageResource(R.drawable.ic_baseline_female_24);
        if (patient.getAppointmentDate().equals(""))
            appointmentDate.setText("No appointment date scheduled.");

        setupViewPager();
    }


    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(Constants.INFLATE_VIEW_PAGER_2_LAYOUT,
                patient.getImages());
        viewPager.setAdapter(adapter);

        HelperMethods.setUpViewPagerIndicator(this, viewPager, viewPagerIndicatorLayout,
                patient.getImages().size());
    }


    private void getDataFromFireStore() {

        LoadingDialog loadingDialog = new LoadingDialog();
        loadingDialog.show(getSupportFragmentManager(), "LOADING_DIALOG");

        FirebaseFirestore.getInstance()
                .collection(getString(R.string.DB_USERS))
                .document(FirebaseAuth.getInstance().getUid())
                .collection(getString(R.string.DB_USERS_PATIENTS))
                .document(patient.getPatientId())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                patient = task.getResult().toObject(Patient.class);
                if (patient != null) {
                    setWidgets();
                } else
                    HelperMethods.showToastInCenter(this);
                loadingDialog.dismiss();
            } else
                HelperMethods.showToastInCenter(this, task.getException().getMessage());
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PATIENT_DATA_CHANGED && resultCode == Activity.RESULT_OK)
            getDataFromFireStore();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }
}