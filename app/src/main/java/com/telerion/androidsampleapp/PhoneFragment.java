package com.telerion.androidsampleapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.telerion.androidsdk.SessionType;

public class PhoneFragment extends Fragment implements View.OnClickListener {

    // Lifecycle overrides.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.phone_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Listeners
        getView().findViewById(R.id.phone_call_button).setOnClickListener(this);
        getView().findViewById(R.id.phone_chat_button).setOnClickListener(this);
        getView().findViewById(R.id.phone_video_button).setOnClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.phone_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.phone_action_logout:
                ((MainActivity)getActivity()).logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // View.OnClickListener overrides.
    @Override
    public void onClick(View v) {
        String remoteNumber = ((TextInputEditText)getView().findViewById(R.id.phone_remote_number)).getText().toString();
        String ownNumber = ((TextView)getView().findViewById(R.id.phone_own_number)).getText().toString();
        if (ownNumber.length() == 0)
            ownNumber = null;

        switch (v.getId()) {
            case R.id.phone_call_button:
                ((MainActivity)getActivity()).makeSession(remoteNumber, ownNumber, false, SessionType.CALL);
                break;
            case R.id.phone_chat_button:
                ((MainActivity)getActivity()).makeSession(remoteNumber, ownNumber, false, SessionType.CHAT);
                break;
            case R.id.phone_video_button:
                ((MainActivity)getActivity()).makeSession(remoteNumber, ownNumber, true, SessionType.CALL);
                break;
        }
    }
}
