package com.telerion.androidsampleapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

public class LoginFragment extends Fragment implements View.OnClickListener {

    // Lifecycle overrides.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((ProgressBar)getView().findViewById(R.id.login_progress_bar)).getIndeterminateDrawable().setColorFilter(0xFF404040, android.graphics.PorterDuff.Mode.MULTIPLY);
        getView().findViewById(R.id.login_progress_bar).setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().findViewById(R.id.login_button).setOnClickListener(this);
    }

    // View.OnClickListener overrides.
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                // Get connection information.
                String server = ((TextInputEditText)getView().findViewById(R.id.edit_server)).getText().toString();
                String login = ((TextInputEditText)getView().findViewById(R.id.edit_login)).getText().toString();
                v.setEnabled(false);
                getView().findViewById(R.id.login_progress_bar).setVisibility(View.VISIBLE);

                // Try to connect phone.
                ((MainActivity)getActivity()).login(server, login);
        }
    }
}
