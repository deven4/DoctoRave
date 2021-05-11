package com.example.doctorave.HomeActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doctorave.ModelClasses.Patient;
import com.example.doctorave.ModelClasses.PatientImages;
import com.example.doctorave.R;
import com.example.doctorave.Utils.Constants;
import com.example.doctorave.Utils.HelperMethods;
import com.example.doctorave.Utils.ImageGridModel;
import com.example.doctorave.Utils.ImageGridRecyclerViewAdapt;
import com.example.doctorave.Utils.LoadingDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ImageGrid extends AppCompatActivity implements ImageGridRecyclerViewAdapt.ImageGridInterface {

    private static final String TAG = "IMAGE_GRID";

    Toolbar toolbar;
    Patient patient;
    boolean isImgDeleted = false;
    RecyclerView recyclerView;
    int selectedImageCount = 0;
    LoadingDialog loadingDialog;
    ImageGridRecyclerViewAdapt adapt;
    LinearLayoutManager layoutManager;

    List<String> mImages = new ArrayList<>();
    List<String> deleteImages = new ArrayList<>();
    List<ImageGridModel> sortedDate = new ArrayList<>();
    List<PatientImages> mPatientImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_grid);

        recyclerView = findViewById(R.id.recyclerView2);
        toolbar = findViewById(R.id.toolbar4);
        loadingDialog = new LoadingDialog();

        toolbar.getMenu().findItem(R.id.edit_icon).setVisible(false);
        toolbar.getMenu().findItem(R.id.delete).setVisible(false);

        toolbar.getMenu().findItem(R.id.delete).setOnMenuItemClickListener(item -> {

            if (deleteImages.size() == 0)
                HelperMethods.showToastInCenter(this, "No Image Selected to delete.");
            else if (deleteImages.size() == patient.getImages().size())
                HelperMethods.showToastInCenter(this, "You cannot delete all images. You need to keep at least one.");
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Confirm Delete");
                builder.setMessage("Are you sure you want to delete selected images?");
                builder.setPositiveButton("Yes", (dialog, which) -> deleteSelectedImages());
                builder.setNegativeButton("Cancel", null);
                builder.create().show();
            }
            return false;
        });

        toolbar.setNavigationOnClickListener(v -> {
            if (checkIsGridItemSelected())
                removeSelectedItems();
            else {
                if (isImgDeleted)
                    setResult(Activity.RESULT_OK);
                finish();
            }
        });

        if (getIntentData())
            setWidgets();
        else {
            HelperMethods.showToastInCenter(this, "Something went wrong. please try again later.");
            finish();
        }
    }


    private void getImagesFromFireStore() {

        FirebaseFirestore.getInstance()
                .collection(getString(R.string.DB_USERS))
                .document(FirebaseAuth.getInstance().getUid())
                .collection(getString(R.string.DB_USERS_PATIENTS))
                .document(patient.getPatientId())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                sortedDate.clear();
                patient = task.getResult().toObject(Patient.class);
                if (patient != null) {
                    sortedDate = sortDate(patient.getImages());
                    Log.d(TAG, "getImagesFromFireStore: " + sortedDate.size());
                    setAdapter();
                } else
                    HelperMethods.showToastInCenter(this, task.getException().getMessage());
                loadingDialog.dismiss();
            }
        });
    }


    private void deleteSelectedImages() {

        loadingDialog.show(getSupportFragmentManager(), "LOADING_DIALOG");
        mPatientImages.clear();
        List<PatientImages> patientImages = patient.getImages();
        Log.d(TAG, "deleteSelectedImages: " + deleteImages.size());

        for (String images : deleteImages) {

            //Delete  Images From Firebase Storage
            FirebaseStorage.getInstance().getReferenceFromUrl(images).delete();

            for (PatientImages patientImg : patientImages) {
                if (patientImg.getImage().equals(images))
                    mPatientImages.add(patientImg);
            }
        }

        DocumentReference documentReference = FirebaseFirestore.getInstance()
                .collection(getString(R.string.DB_USERS))
                .document(FirebaseAuth.getInstance().getUid())
                .collection(getString(R.string.DB_USERS_PATIENTS))
                .document(patient.getPatientId());

        for (PatientImages ob1 : mPatientImages) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("dateCreated", ob1.getDateCreated());
            hashMap.put("image", ob1.getImage());
            documentReference.update("images", FieldValue.arrayRemove(hashMap))
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            HelperMethods.showToastInCenter(this, "Images Deleted");
                            updateImgRecyclerView();
                        } else
                            Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }


    private void updateImgRecyclerView() {

        for (String deletedImg : deleteImages) {
            for (Iterator<ImageGridModel> mAllImg = sortedDate.iterator(); mAllImg.hasNext();) {
                List<String> images = mAllImg.next().getImages();
                images.remove(deletedImg);

                if (images.size() == 0)
                    sortedDate.remove(mAllImg.next());
            }
        }


        isImgDeleted = true;
        adapt.notifyDataSetChanged();
        removeSelectedItems();
        loadingDialog.dismiss();
    }


    private boolean checkIsGridItemSelected() {

        if (adapt != null) {
            return adapt.isSelected;
        } else
            return false;
    }


    private void removeSelectedItems() {

        for (int i = 0; i < layoutManager.getItemCount(); i++) {
            ImageGridRecyclerViewAdapt.mViewHolderClass viewHolderClass =
                    (ImageGridRecyclerViewAdapt.mViewHolderClass) recyclerView.findViewHolderForAdapterPosition(i);
            if (viewHolderClass != null) {
                for (int j = 0; j < viewHolderClass.gridView.getAdapter().getCount(); j++) {
                    View view = viewHolderClass.gridView.getChildAt(j);
                    LinearLayout linearLayout = view.findViewById(R.id.linearLayout9);
                    linearLayout.setVisibility(View.GONE);
                }
            }
        }

        adapt.isSelected = false;
        selectedImageCount = 0;
        deleteImages.clear();
        toolbar.setTitle(patient.getName());
        toolbar.getMenu().findItem(R.id.delete).setVisible(false);
    }


    private void setWidgets() {

        sortedDate = sortDate(patient.getImages());

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        setAdapter();
        adapt.setLongClickListener(this);
    }


    private void setAdapter() {
        adapt = new ImageGridRecyclerViewAdapt(this, sortedDate, patient);
        recyclerView.setAdapter(adapt);
    }


    private List<ImageGridModel> sortDate(List<PatientImages> images) {

        List<ImageGridModel> mList = new ArrayList<>();

        for (int i = 0; i < images.size(); i++) {
            if (i + 1 < images.size()) {
                PatientImages ob1 = images.get(i);
                PatientImages ob2 = images.get(i + 1);
                if (getDateFromMillis(ob1.getDateCreated())
                        .equals(getDateFromMillis(ob2.getDateCreated()))) {
                    if (!mImages.contains(ob1.getImage())) {
                        mImages.add(ob1.getImage());
                    }
                } else {
                    if (mImages.size() == 0)
                        mImages.add(ob1.getImage());
                    mList.add(new ImageGridModel(getDateFromMillis(ob1.getDateCreated()), new ArrayList<>(mImages)));
                    mImages.clear();
                }
                if (!mImages.contains(ob2.getImage())) {
                    mImages.add(ob2.getImage());
                }
            } else {
                if (i == images.size() - 1) {
                    if (!mImages.contains(images.get(i).getImage()))
                        mImages.add(images.get(i).getImage());
                    mList.add(new ImageGridModel(getDateFromMillis(images.get(i).getDateCreated()), mImages));
                }
            }
        }

//        for (ImageGridModel ob : mList) {
//            Log.d(TAG, "sortDate: " + ob.getDate());
//            Log.d(TAG, "sortImage: " + ob.getImages());
//        }
        return mList;
    }


    public String getDateFromMillis(long date) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(date);
        Date date1 = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        return simpleDateFormat.format(date1);
    }


    private boolean getIntentData() {

        Intent intent = getIntent();
        patient = intent.getParcelableExtra(Constants.PATIENT_OBJECT);
        return patient != null;
    }


    public void showItemSelectedWidgets() {

        toolbar.setTitle(selectedImageCount + " item selected");
        MenuItem menuItem = toolbar.getMenu().findItem(R.id.delete);
        if (!menuItem.isVisible())
            menuItem.setVisible(true);
    }


    @Override
    public void onItemLongClick(View view, int position, int adapterPosition) {

        LinearLayout selectedImg = view.findViewById(R.id.linearLayout9);
        if (selectedImg.getVisibility() == View.VISIBLE) {
            selectedImageCount -= 1;
            selectedImg.setVisibility(View.GONE);
            deleteImages.remove(sortedDate.get(adapterPosition).getImages().get(position));
        } else {
            selectedImageCount += 1;
            selectedImg.setVisibility(View.VISIBLE);
            deleteImages.add(sortedDate.get(adapterPosition).getImages().get(position));
        }
        showItemSelectedWidgets();
    }


    @Override
    public void onBackPressed() {

        if (checkIsGridItemSelected())
            removeSelectedItems();
        else {
            if (isImgDeleted)
                setResult(Activity.RESULT_OK);
            super.onBackPressed();
        }
    }
}