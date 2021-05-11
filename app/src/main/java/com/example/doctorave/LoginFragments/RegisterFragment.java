package com.example.doctorave.LoginFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.doctorave.HomeActivity.HomeActivity;
import com.example.doctorave.ModelClasses.User;
import com.example.doctorave.R;
import com.example.doctorave.Utils.Constants;
import com.example.doctorave.Utils.HelperMethods;
import com.example.doctorave.Utils.LoadingDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.regex.Pattern;

public class RegisterFragment extends Fragment {

    LinearLayout linearLayout;
    MaterialButton registerBtn;
    FirebaseAuth firebaseAuth;
    LoadingDialog loadingDialog;
    FirebaseFirestore firebaseFirestore;
    TextInputLayout fullName, emailId, pass, reEnterPass;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Reference Created
        pass = view.findViewById(R.id.textInputLayout3);
        fullName = view.findViewById(R.id.textInputLayout);
        emailId = view.findViewById(R.id.textInputLayout2);
        linearLayout = view.findViewById(R.id.linearLayout);
        reEnterPass = view.findViewById(R.id.textInputLayout4);
        registerBtn = view.findViewById(R.id.materialButton3);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        // Listener to check if all fields are filled and enabling the Register btn.
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = fullName.getEditText().getText().toString();
                String email = emailId.getEditText().getText().toString();
                String password = pass.getEditText().getText().toString();
                String pass2 = reEnterPass.getEditText().getText().toString();
                registerBtn.setEnabled(!name.equals("") && !email.equals("") && !password.equals("") && !pass2.equals(""));
            }
        };

        fullName.getEditText().addTextChangedListener(textWatcher);
        emailId.getEditText().addTextChangedListener(textWatcher);
        pass.getEditText().addTextChangedListener(textWatcher);
        reEnterPass.getEditText().addTextChangedListener(textWatcher);


        // Navigating back to Login Fragment
        linearLayout.setOnClickListener(v -> getFragmentManager().beginTransaction()
                .replace(R.id.login_fragment_holder, new LoginFragment())
                .addToBackStack(Constants.LOGIN_FRAGMENT_TAG)
                .commit());
        registerBtn.setOnClickListener(v -> checkCredentials());

        return view;
    }


    private void checkCredentials() {

        HelperMethods.HideKeyboard(getActivity());

        emailId.setError("");
        pass.setError("");
        reEnterPass.setError("");

        String name = fullName.getEditText().getText().toString();
        String email = emailId.getEditText().getText().toString().trim();
        String password = pass.getEditText().getText().toString();
        String pass2 = reEnterPass.getEditText().getText().toString();

        if (!HelperMethods.validateEmailId(email))
            emailId.setError("Please enter a valid email address.");
        else if (!(password.length() > 6))
            pass.setError("Password must have 6 or more characters.");
        else if (!password.matches(pass2))
            reEnterPass.setError("Password doesn't match.");
        else {
            registerNewUser(name, email, password);
        }
    }


    private void registerNewUser(String name, String email, String password) {

        loadingDialog = new LoadingDialog();
        assert getFragmentManager() != null;
        loadingDialog.show(getFragmentManager(), "LOADING_DIALOG");

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) Objects.requireNonNull(getContext()), task -> {
                    if (task.isSuccessful()) {
                        String uid = task.getResult().getUser().getUid();
                        addUserDataIntoFireStore(uid, name, email);
                    } else {
                        Toast toast = Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        loadingDialog.dismiss();
                    }
                });
    }


    private void addUserDataIntoFireStore(String uid, String name, String email) {

        User user = new User(uid, name, email, "", null);

        firebaseFirestore.collection("Users")
                .document(uid)
                .set(user)
                .addOnCompleteListener((Activity) getContext(), task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(getContext(), HomeActivity.class));
                        getActivity().finish();
                    } else {
                        Toast toast = Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                    loadingDialog.dismiss();
                });
    }
}
