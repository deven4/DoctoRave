package com.example.doctorave.LoginFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.doctorave.R;
import com.example.doctorave.Utils.Constants;
import com.google.android.material.button.MaterialButton;

public class BaseFragment extends Fragment {

    MaterialButton registerBtn, loginBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, container, false);

        registerBtn = view.findViewById(R.id.materialButton);
        loginBtn = view.findViewById(R.id.materialButton2);

        registerBtn.setOnClickListener(v -> getFragmentManager().beginTransaction().replace(R.id.login_fragment_holder, new RegisterFragment())
                .addToBackStack(Constants.LOGIN_FRAGMENT_TAG)
                .commit());
        loginBtn.setOnClickListener(v -> getFragmentManager().beginTransaction().replace(R.id.login_fragment_holder, new LoginFragment())
                .addToBackStack(Constants.LOGIN_FRAGMENT_TAG)
                .commit());

        return view;
    }
}
