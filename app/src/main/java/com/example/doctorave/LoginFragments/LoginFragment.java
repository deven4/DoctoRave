package com.example.doctorave.LoginFragments;

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
import com.example.doctorave.R;
import com.example.doctorave.Utils.Constants;
import com.example.doctorave.Utils.HelperMethods;
import com.example.doctorave.Utils.LoadingDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    LinearLayout signUpBtn;
    MaterialButton loginBtn;
    TextInputLayout emailId, password;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        loginBtn = view.findViewById(R.id.materialButton4);
        emailId = view.findViewById(R.id.textInputLayout5);
        password = view.findViewById(R.id.textInputLayout6);
        signUpBtn = view.findViewById(R.id.linearLayout2);


        // Listener for Enabling Login Btn as soon as both editText are filled.
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                loginBtn.setEnabled(!emailId.getEditText().getText().toString().equals("")
                        && !password.getEditText().getText().toString().equals(""));
            }
        };


        emailId.getEditText().addTextChangedListener(textWatcher);
        password.getEditText().addTextChangedListener(textWatcher);

        loginBtn.setOnClickListener(a -> checkCredentials());
        signUpBtn.setOnClickListener(v -> getFragmentManager().beginTransaction()
                .replace(R.id.login_fragment_holder, new RegisterFragment())
                .addToBackStack(Constants.LOGIN_FRAGMENT_TAG)
                .commit());

        return view;
    }


    private void checkCredentials() {

        emailId.setError("");
        password.setError("");

        String email = emailId.getEditText().getText().toString().trim();
        String pass = password.getEditText().getText().toString();

        if (!HelperMethods.validateEmailId(email))
            emailId.setError("Please enter a valid Email Address.");
        else
            loginUser(email, pass);
    }


    private void loginUser(String email, String pass) {

        LoadingDialog loadingDialog = new LoadingDialog();
        assert getFragmentManager() != null;
        loadingDialog.show(getFragmentManager(), "LOADING_DIALOG");

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(getActivity(), task -> {
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
