package com.example.doctorave.HomeActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.doctorave.ModelClasses.Patient;
import com.example.doctorave.R;
import com.example.doctorave.Utils.HelperMethods;
import com.example.doctorave.Utils.RecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.doctorave.Utils.Constants.NEW_PATIENT_ADDED;

public class HomeFragment extends Fragment {

    private static final String TAG = "HOME_FRAGMENT";

    int patientCount = 0;
    ProgressBar progressBar;
    FloatingActionButton fab;
    RecyclerView recyclerView;
    LinearLayout noPatientFound;
    ConstraintLayout mainLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerViewAdapter recyclerViewAdapter;

    FirebaseUser firebaseUser;
    activityCommunicator mListener;
    CollectionReference collectionReference;
    ArrayList<Patient> mPatients = new ArrayList<>();
    ArrayList<Patient> mAllPatients = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mainLayout = view.findViewById(R.id.mainLayout);
        fab = view.findViewById(R.id.floatingActionButton);
        progressBar = view.findViewById(R.id.progressBar1);
        recyclerView = view.findViewById(R.id.recyclerView);
        noPatientFound = view.findViewById(R.id.linearLayout5);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        collectionReference = FirebaseFirestore.getInstance().collection(getString(R.string.DB_USERS))
                .document(firebaseUser.getUid())
                .collection(getString(R.string.DB_USERS_PATIENTS));

        fab.setOnClickListener(v -> startActivityForResult(new Intent(getContext(), AddNewPatient.class), NEW_PATIENT_ADDED));
        swipeRefreshLayout.setOnRefreshListener(() -> {
            patientCount = mPatients.size();
            getPatients();
        });

        getCommunicator();
        setAdapter();
        getPatients();

        return view;
    }


    private void getCommunicator() {

        ((HomeActivity) getActivity()).passCommunicator(text -> {
            Log.d(TAG, "mAllPatient Size: " + mAllPatients.size());
            mPatients.clear();
            if (text.equals("") || text == null) {
                mPatients.addAll(mAllPatients);
            } else {
                String str = text.toLowerCase().trim();
                for (Patient patient : mAllPatients) {
                    if (patient.getName().toLowerCase().contains(str)
                            && !mPatients.contains(patient))
                        mPatients.add(patient);
                }
            }
            recyclerViewAdapter.notifyDataSetChanged();
        });
    }


    private void getPatients() {

        collectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                mAllPatients.clear();
                mPatients.clear();
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    if (documentSnapshot != null) {
                        Patient patient = documentSnapshot.toObject(Patient.class);
                        mPatients.add(patient);
                    }
                }
                mAllPatients.addAll(mPatients);
                if (mPatients.size() == 0) {
                    noPatientFound.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                } else if (patientCount == mPatients.size())
                    HelperMethods.showToastInCenter(getContext(), "No New Patient Found.");

                recyclerViewAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                mainLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            } else
                HelperMethods.showToastInCenter(getContext(), Objects.requireNonNull(task.getException()).getMessage());
        });
    }


    private void setAdapter() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(), mPatients);
        recyclerView.setAdapter(recyclerViewAdapter);
    }


    public void setActivityListener(activityCommunicator mListener) {
        this.mListener = mListener;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_PATIENT_ADDED && resultCode == Activity.RESULT_OK) {
            progressBar.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.GONE);
            getPatients();
        }
    }


    public interface activityCommunicator {
        void inflateNavHeader(Patient patient);
    }
}
