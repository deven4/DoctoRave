package com.example.doctorave.HomeActivity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.doctorave.ModelClasses.Patient;
import com.example.doctorave.ModelClasses.PatientImages;
import com.example.doctorave.R;
import com.example.doctorave.Utils.Constants;
import com.example.doctorave.Utils.HelperMethods;
import com.example.doctorave.Utils.LoadingDialog;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;


public class AddNewPatient extends AppCompatActivity {

    private static final int CAMERA_REQ_CODE = 100;
    private static final int GALLERY_REQ_CODE = 101;
    private static final int PERMISSIONS_REQ_CODE = 102;
    private static final String TAG = "ADD_PATIENT";

    String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    Patient patient;
    ImageView imageView;
    RadioGroup radioGroup;
    CardView addMorePhotos;
    boolean isEditPatient;
    LoadingDialog loadingDialog;
    MaterialButton materialButton;
    MaterialToolbar materialToolbar;
    TextInputLayout fullName, emailId, mobileNo, age, illness, appointmentDate,
            notes;

    RadioButton maleRad, femaleRad, transRad;
    List<String> images = new ArrayList<>();
    List<PatientImages> patientImages = new ArrayList<>();

    FirebaseUser currentUser;
    CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        maleRad = findViewById(R.id.rad1);
        transRad = findViewById(R.id.rad3);
        femaleRad = findViewById(R.id.rad2);
        imageView = findViewById(R.id.imageView7);
        radioGroup = findViewById(R.id.radioGroup);
        age = findViewById(R.id.textInputLayout10);
        notes = findViewById(R.id.textInputLayout14);
        addMorePhotos = findViewById(R.id.cardView2);
        materialToolbar = findViewById(R.id.toolbar2);
        emailId = findViewById(R.id.textInputLayout8);
        mobileNo = findViewById(R.id.textInputLayout11);
        illness = findViewById(R.id.textInputLayout12);
        fullName = findViewById(R.id.textInputLayout7);
        materialButton = findViewById(R.id.materialButton5);
        appointmentDate = findViewById(R.id.textInputLayout13);


        // Firebase Variables
        loadingDialog = new LoadingDialog();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        collectionReference = FirebaseFirestore.getInstance().collection(getString(R.string.DB_USERS))
                .document(currentUser.getUid())
                .collection(getString(R.string.DB_USERS_PATIENTS));


        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                verifyFields();
            }
        };

        Objects.requireNonNull(fullName.getEditText()).addTextChangedListener(textWatcher);
        Objects.requireNonNull(emailId.getEditText()).addTextChangedListener(textWatcher);
        Objects.requireNonNull(mobileNo.getEditText()).addTextChangedListener(textWatcher);
        Objects.requireNonNull(age.getEditText()).addTextChangedListener(textWatcher);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> verifyFields());


        Objects.requireNonNull(illness.getEditText()).setOnClickListener(v -> showDialog());
        Objects.requireNonNull(appointmentDate.getEditText()).setOnClickListener(v -> openCalender());

        imageView.setOnClickListener(v -> {

            if (isPermissionGrantedByUser()) {
                showImageChooserDialog();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    requestPermissions(permissions, PERMISSIONS_REQ_CODE);
            }
        });
        materialButton.setOnClickListener(v -> checkCredentials());
        materialToolbar.setNavigationOnClickListener(v -> finish());

        if (getIntentData()) {
            isEditPatient = true;
            setPatientData();
        } else
            isEditPatient = false;
    }


    private void setPatientData() {

        materialToolbar.setTitle("Edit Patient");

        age.getEditText().setText(String.valueOf(patient.getAge()));
        fullName.getEditText().setText(patient.getName());
        emailId.getEditText().setText(patient.getEmailId());
        mobileNo.getEditText().setText(patient.getMobileNumber());
        illness.getEditText().setText(patient.getIssueIllness());
        appointmentDate.getEditText().setText(patient.getAppointmentDate());
        notes.getEditText().setText(patient.getComments());

        maleRad.setChecked(patient.getGender().equals(maleRad.getText().toString()));
        femaleRad.setChecked(patient.getGender().equals(femaleRad.getText().toString()));
        transRad.setChecked(patient.getGender().equals(transRad.getText().toString()));

        if (patient.getImages().size() != 0)
            Glide.with(this).load(patient.getImages().get(0).getImage()).into(imageView);
        imageView.setPadding(0, 0, 0, 0);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addMorePhotos.setVisibility(View.VISIBLE);
    }


    public boolean getIntentData() {
        patient = getIntent().getParcelableExtra(Constants.PATIENT_OBJECT);
        return patient != null;
    }


    private void checkCredentials() {

        HelperMethods.HideKeyboard(this);
        mobileNo.setError("");
        emailId.setError("");
        emailId.setErrorEnabled(false);
        mobileNo.setErrorEnabled(false);

        if (images.size() == 0 && !isEditPatient)
            HelperMethods.showToastInCenter(this, "Please upload at least one photo of the patient.");
        else if (Objects.requireNonNull(mobileNo.getEditText()).getText().toString().length() != 10)
            mobileNo.setError("Please enter a valid mobile number.");
        else if (!HelperMethods.validateEmailId(emailId.getEditText().getText().toString().trim()))
            emailId.setError("Please enter a valid email address.");
        else if (images.size() == 0 && isEditPatient) {
            loadingDialog.show(getSupportFragmentManager(), "LOADING_DIALOG_2");
            uploadDataIntoFireStore(patient.getPatientId());
        } else
            uploadImagesToStorage();
    }


    private void uploadImagesToStorage() {

        loadingDialog.show(getSupportFragmentManager(), "LOADING_DIALOG");

        String patientId;
        if (isEditPatient)
            patientId = patient.getPatientId();
        else
            patientId = collectionReference.document().getId();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child(currentUser.getUid())
                .child(patientId);

        for (int i = 0; i < images.size(); i++) {
            String image = images.get(i);
            StorageReference mRef = storageReference.child(System.currentTimeMillis() + ".jpg");
            UploadTask uploadTask = mRef.putFile(Uri.parse(image));
            uploadTask.addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    mRef.getDownloadUrl().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            patientImages.add(new PatientImages(String.valueOf(task1.getResult()), System.currentTimeMillis()));
                            Log.d(TAG, "uploadImagesToStorage: " + patientImages.size());
                            if (patientImages.size() == images.size()) {
                                Log.d(TAG, "UPLOADING DATA TO  FIRESTORE");
                                uploadDataIntoFireStore(patientId);
                            }
                        } else {
                            HelperMethods.showToastInCenter(this, Objects.requireNonNull(task1.getException()).getMessage());
                            loadingDialog.dismiss();
                        }
                    });
                } else {
                    HelperMethods.showToastInCenter(this, Objects.requireNonNull(task.getException()).getMessage());
                    loadingDialog.dismiss();
                }
            });
        }
    }


    private void uploadDataIntoFireStore(String patientId) {

        String mName = Objects.requireNonNull(fullName.getEditText()).getText().toString();
        String mEmail = Objects.requireNonNull(emailId.getEditText()).getText().toString();
        String mobNo = Objects.requireNonNull(mobileNo.getEditText()).getText().toString();
        String mAge = Objects.requireNonNull(age.getEditText()).getText().toString();
        String mIssue = Objects.requireNonNull(illness.getEditText()).getText().toString();
        String mAptDate = Objects.requireNonNull(appointmentDate.getEditText()).getText().toString();
        String mNotes = Objects.requireNonNull(notes.getEditText()).getText().toString();
        RadioButton mRad = findViewById(radioGroup.getCheckedRadioButtonId());

        if (isEditPatient) {
            patientImages.addAll(patient.getImages());
        }
        Patient patientObject = new Patient(Integer.parseInt(mAge), patientImages, patientId,
                mName, mRad.getText().toString(), mEmail.trim(), mobNo, mAptDate, mNotes, mIssue);

        collectionReference.document(patientId)
                .set(patientObject)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String msg = "Patient added successfully.";
                        if (isEditPatient)
                            msg = "Patient's data changed successfully.";
                        HelperMethods.showToastInCenter(this, msg);
                        setResult(Activity.RESULT_OK);
                        finish();
                    } else {
                        HelperMethods.showToastInCenter(this, Objects.requireNonNull(task.getException()).getMessage());
                    }
                    loadingDialog.dismiss();
                });

        loadingDialog.dismiss();
    }


    private void showImageChooserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Upload Patient image");
        CharSequence[] options = new CharSequence[]{"Camera", "Gallery"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0)
                openCamera();
            else
                openGallery();
        });
        builder.show();
    }


    private boolean isPermissionGrantedByUser() {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQ_CODE);
    }


    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQ_CODE);
    }


    private void openCalender() {

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {

            String chosenDate = dayOfMonth + "-" + month + "-" + year;
            Objects.requireNonNull(appointmentDate.getEditText()).setText(chosenDate);
        };

        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }


    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Health Issue");
        CharSequence[] items = new CharSequence[]{
                "Blood", "Cancer and neoplasms", "Cardiovascular",
                "Congenital disorders", "Ear", "Eye",
                "Infection", "Inflammatory and Immune System",
                "Injuries and accidents", "Mental health",
                "Metabolic and endocrine", "Musculoskeletal",
                "Neurological", "Oral and gastrointestinal",
                "Renal and urogenital", "Reproductive health and childbirth",
                "Respiratory", "Skin", "Stroke", "Generic health relevance",
                "Disputed aetiology and other",
        };
        builder.setItems(items, (dialog, which) -> {
            Objects.requireNonNull(illness.getEditText()).setText(items[which]);
        });
        builder.create().show();
    }


    private void verifyFields() {
        materialButton.setEnabled(!fullName.getEditText().getText().toString().equals("")
                && !emailId.getEditText().getText().toString().equals("")
                && !mobileNo.getEditText().getText().toString().equals("")
                && !age.getEditText().getText().toString().equals("")
                && radioGroup.getCheckedRadioButtonId() != -1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        boolean flag;

        if (data != null) {
            flag = true;
            Uri uri = null;
            if (requestCode == GALLERY_REQ_CODE) {
                uri = data.getData();
                imageView.setImageURI(data.getData());
            } else if (requestCode == CAMERA_REQ_CODE) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                assert bitmap != null;
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, bytes);
                String image = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
                Log.d(TAG, "onActivityResult: " + image);
                if (image != null) {
                    uri = Uri.parse(image);
                    imageView.setImageURI(uri);
                } else {
                    Toast.makeText(this, "Couldn't upload image. Please try again later.", Toast.LENGTH_SHORT).show();
                    flag = false;
                }
            }

            if (flag && uri != null) {
                imageView.setPadding(0, 0, 0, 0);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                images.add(String.valueOf(uri));
                addMorePhotos.setVisibility(View.VISIBLE);
            }
        } else
            Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        boolean flag = false;
        if (requestCode == PERMISSIONS_REQ_CODE) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                Toast toast = Toast.makeText(this, "Please provide the requested permissions to proceed",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else
                showImageChooserDialog();
        }
    }
}